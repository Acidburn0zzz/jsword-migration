/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book.raw;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.crosswire.common.util.Logger;

/**
 * The WordItemsMem stores words in a dictionary for a Bible.
 * The single method that will be of use 99% of the time is the
 * <code>getWord(int)</code> method. This method will be called once for
 * each word every time we display a verse (Assuming that we have not
 * implemented any caches).
 * <p>The <code>getIndex(String)</code> method is the reverse of this, and
 * is used in creating the index in the first place.
 * <p>The class has an underlying File however this is transarent to the
 * user, since calls to getIndex(String) have any disk changes
 * automatically written to disk, and the implementation of this class must
 * be free to choose whatever cacheing scheme it needs.
 * <p>The index file size will be roughly n*(a+v) where:<ul>
 * <li>n is the number of words. (=~16,000)
 * <li>a is the average word length. (=~8)
 * <li>v is the overhead per word. (=~1)
 * </ul>
 * This would give an index file size of 150k. I need to check with the
 * OLB and with Theopholos, however I think this compares favorably. It
 * would make the smallest download that contained Bible text (but no
 * punctuation or case marks, etc) under 200k before compression, maybe
 * under 150k after. A full basic extensible OLB in under 200k would be
 * a achievement and well under a 2 minute download.
 *
 * <h3>Index File Structure</h3>
 * I expect that the general layout will be something like:<pre>
 * 0 -.    \
 * 1 -+.   !
 * 2 -++.  ! index area
 * .  !!!  !
 * .  !!!  /
 * a <'!!  \
 * a   !!  !
 * r   !!  !
 * o   !!  ! text area
 * n   !!  !
 * a <-'!  !
 * b    !  !
 * .    !  /
 * </pre>
 * For this layout we can use the index of word (n+1) to calculate the
 * length of word (n) (so long as the words are in index order in the text
 * area. This would make v=1 (for the index). We could even use upper case
 * letters to mark new words - this would mean we could have an out of
 * order text area, <i>or</i> no index area (i.e. v=0) However having v=0
 * would force us to do in memory cacheing.
 * <p>The OLB v8 seems to do some form of (offset,length) indexing to
 * compress files sizes further (or is it simply to obfuscate the file
 * format?) I'd rather use .zip technology for compression.
 *
 * <p>Consider whether and to what extent this class should be static and
 * public. I think that it should be package scope - Use of this class
 * does not make sense outside of the RawBook package. There should
 * only ever be one WordIndex for a given file, but if we can instantiate
 * this class for several sets of files - it does not make sense to make
 * it static.
 *
 * <p>How can we extend this class in the future?<ul>
 * <li>Various different cacheing methods, so that we can work in low
 *     memory conditions. This is totally internal to this class, and
 *     does not affect the interface at all.
 * <li>Inheritance. There are various classes that do a similar job of
 *     reading from files from similar locations.
 * </ul>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class WordItemsMem extends ItemsMem {
    /**
     * Create a WordMemResourceIndex from a File that contains the dictionary.
     * 
     * @param raw
     *            Reference to the RawBook that is using us
     * @param create
     *            Should we start all over again
     */
    public WordItemsMem(RawBook raw, boolean create) throws IOException {
        super(raw, RawConstants.FILE_WORD_ITEM, create);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.raw.Items#getIndex(java.lang.String)
     */
    @Override
    public int getIndex(String data) {
        return super.getIndex(data.toLowerCase());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.raw.ItemsMem#getMaxItems()
     */
    @Override
    public int getMaxItems() {
        return 20000;
    }

    /**
     * Find a list of words that start with the given word
     * 
     * @param word
     *            The word to search for
     * @return An array of matches
     */
    public Collection getStartsWith(String word) {
        assert array != null;

        List list = new ArrayList();
        word = word.toLowerCase();

        // This bit is s_l_o_w. We do a one end to the other search through all
        // the words for starts-with matches, putting the results into a vector.
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                log.warn("null word at index " + i);
            } else {
                if (array[i].startsWith(word)) {
                    list.add(array[i]);
                }
            }
        }

        return list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.raw.Mem#load(java.io.InputStream)
     */
    @Override
    public void load(InputStream in) throws IOException {
        DataInputStream din = new DataInputStream(in);

        byte[] asig = new byte[6];
        din.readFully(asig);

        String ssig = new String(asig);
        assert ssig.equals(RawConstants.SIG_WORD_ITEM);

        count = din.readInt();
        hash = new Hashtable(count);
        array = new String[count];

        for (int i = 0; i < count; i++) {
            byte wordlen = din.readByte();
            byte[] aword = new byte[wordlen];
            din.readFully(aword);
            String word = new String(aword);

            hash.put(word, Integer.valueOf(i));
            array[i] = word;
        }

        din.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.raw.Mem#save(java.io.OutputStream)
     */
    @Override
    public void save(OutputStream out) throws IOException {
        DataOutputStream dout = new DataOutputStream(out);

        dout.writeBytes(RawConstants.SIG_WORD_ITEM);
        dout.writeInt(hash.size());

        for (int i = 0; i < hash.size(); i++) {
            dout.writeByte(array[i].length());
            dout.writeBytes(array[i]);
        }

        dout.close();
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(WordItemsMem.class);
}
