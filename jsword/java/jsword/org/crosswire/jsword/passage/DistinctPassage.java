
package org.crosswire.jsword.passage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A Passage that is implemented using a TreeSet of Verses.
 * The attributes of the style are:<ul>
 * <li>Fairly fast manipulation
 * <li>Slow getName()
 * <li>Bloated for storing large numbers of Verses
 * </ul>
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class DistinctPassage extends AbstractPassage
{
    /**
     * Create an empty DistinctPassage. There are no ctors from either
     * Verse or VerseRange so you need to do new
     * <code>DistinctPassage().add(...);</code>
     */
    protected DistinctPassage()
    {
    }

    /**
     * Create a Verse from a human readable string. The opposite
     * of toString(), Given any DistinctPassage v1, and the following
     * <code>DistinctPassage v2 = new DistinctPassage(v1.toString());</code>
     * Then <code>v1.equals(v2);</code>
     * Theoretically, since there are many ways of representing a
     * DistinctPassage as text string comparision along the lines of:
     * <code>v1.toString().equals(v2.toString())</code> could be false.
     * Practically since toString() is standardized this will be true
     * however. We don't need to worry about thread safety in a ctor since
     * we don't exist yet.
     * @param refs A String containing the text of the DistinctPassage
     * @throws NoSuchVerseException If the string is not valid
     */
    protected DistinctPassage(String refs) throws NoSuchVerseException
    {
        super(refs);

        store = Collections.synchronizedSortedSet(new TreeSet());
        addVerses(refs);
    }

    /**
     * Get a copy of ourselves. Points to note:<ul>
     * <li>Call clone() not new() on member Objects, and on us.
     * <li>Do not use Copy Constructors! - they do not inherit well.
     * <li>Think about this needing to be synchronized
     * <li>If this is not cloneable then writing cloneable children is harder
     * </ul>
     * @return A complete copy of ourselves
     * @exception CloneNotSupportedException We don't do this but our kids might
     */
    public Object clone() throws CloneNotSupportedException
    {
        // This gets us a shallow copy
        DistinctPassage copy = (DistinctPassage) super.clone();

        // I want to just do the following
        //   copy.store = (SortedSet) store.clone();
        // However SortedSet is not Clonable so I can't
        // Watch out for this, I'm not sure if it breaks anything.
        copy.store = Collections.synchronizedSortedSet(new TreeSet());
        copy.store.addAll(store);

        return copy;
    }

    /**
     * Iterate over the Verses
     * @return A list enumerator
     */
    public Iterator verseIterator()
    {
        return store.iterator();
    }

    /**
     * @returns true if this Passage contains no Verses
     */
    public boolean isEmpty()
    {
        return store.isEmpty();
    }

    /**
     * @return the number of Verses in this Passage
     */
    public int countVerses()
    {
        return store.size();
    }

    /**
     * Returns true if this Passage contains the specified Verse.
     * @param obj Verse whose presence in this Passage is to be tested.
     * @return true if this Passage contains the specified Verse
     */
    public boolean contains(VerseBase obj)
    {
        Verse[] verses = toVerseArray(obj);

        for (int i=0; i<verses.length; i++)
            if (!store.contains(verses[i])) return false;

        return true;
    }

    /**
     * Ensures that this Passage contains the specified Verse
     * @param obj Verse whose presence in this Passage is to be ensured
     */
    public void add(VerseBase obj)
    {
        optimizeWrites();

        Verse[] verses = toVerseArray(obj);

        for (int i=0; i<verses.length; i++)
            store.add(verses[i]);

        // we do an extra check here because the cost of calculating the
        // params is non-zero an may be wasted
        if (suppress_events == 0)
            fireIntervalAdded(this, verses[0], verses[verses.length-1]);
    }

    /**
     * Removes a single instance of the specified Verse from this Passage
     * @param obj Verse to be removed from this Passage, if present.
     */
    public void remove(VerseBase obj)
    {
        optimizeWrites();

        Verse[] verses = toVerseArray(obj);

        for (int i=0; i<verses.length; i++)
            store.remove(verses[i]);

        // we do an extra check here because the cost of calculating the
        // params is non-zero an may be wasted
        if (suppress_events == 0)
            fireIntervalRemoved(this, verses[0], verses[verses.length-1]);
    }

    /**
     * Removes all of the Verses from this Passage.
     */
    public void clear()
    {
        optimizeWrites();

        store.clear();
        fireIntervalRemoved(this, null, null);
    }

    /**
     * Call the support mechanism in AbstractPassage
     * @param out The stream to write our state to
     * @throws IOException if the read fails
     * @serialData Write the ordinal number of this verse
     * @see AbstractPassage#writeObjectSupport(ObjectOutputStream)
     */
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        writeObjectSupport(out);
    }

    /**
     * Call the support mechanism in AbstractPassage
     * @param in The stream to read our state from
     * @throws IOException if the read fails
     * @throws ClassNotFoundException If the read data is incorrect
     * @serialData Write the ordinal number of this verse
     * @see AbstractPassage#readObjectSupport(ObjectInputStream)
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        optimizeWrites();

        store = Collections.synchronizedSortedSet(new TreeSet());
        readObjectSupport(in);
    }

    /** To make serialization work across new versions */
    static final long serialVersionUID = 817374460730441662L;

    /** The place the real data is stored */
    private transient SortedSet store = Collections.synchronizedSortedSet(new TreeSet());
}
