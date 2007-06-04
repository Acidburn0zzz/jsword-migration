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
package org.crosswire.jsword.book.ser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URI;

import org.crosswire.common.activate.Activatable;
import org.crosswire.common.activate.Activator;
import org.crosswire.common.activate.Lock;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.BibleInfo;

/**
 * A cache of BookData that can be shared amongst Books.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BookDataCache implements Activatable
{
    /**
     * Constructor for BookDataCache.
     */
    public BookDataCache(URI uri) throws MalformedURLException
    {
        this.uri = uri;

        if (!uri.getScheme().equals(NetUtil.PROTOCOL_FILE))
        {
            throw new MalformedURLException(Msg.NON_FILE_URL.toString(uri));
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#activate(org.crosswire.common.activate.Lock)
     */
    public final void activate(Lock lock)
    {
        dataUri = NetUtil.lengthenURI(uri, FILE_DATA);
        indexUri = NetUtil.lengthenURI(uri, FILE_INDEX);

        IOUtil.close(dataRaf);
        IOUtil.close(indexIn);
        try
        {
            // Create blank indexes
            indexArr = new long[BibleInfo.versesInBible()];
        
            // Open the XML RAF
            dataRaf = new RandomAccessFile(NetUtil.getAsFile(dataUri), FileUtil.MODE_READ);

            // Open the index file
            indexIn = new BufferedReader(new InputStreamReader(NetUtil.getInputStream(indexUri)));

            // Load the ascii XML index
            for (int i = 0; i < BibleInfo.versesInBible(); i++)
            {
                String line = null;

                try
                {
                    line = indexIn.readLine();
                }
                catch (IOException ex)
                {
                    log.error("Error reading index", ex); //$NON-NLS-1$
                    break;
                }

                if (line == null)
                {
                    break;
                }

                try
                {
                    indexArr[i] = Integer.parseInt(line);
                }
                catch (NumberFormatException ex)
                {
                    indexArr[i] = -1;
                    log.error("Error parsing line: "+line, ex); //$NON-NLS-1$
                }
            }

            active = true;
        }
        catch (IOException ex)
        {
            log.warn("failed to open stream", ex); //$NON-NLS-1$
        }
        finally
        {
            IOUtil.close(dataRaf);
            IOUtil.close(indexIn);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.activate.Activatable#deactivate(org.crosswire.common.activate.Lock)
     */
    public final void deactivate(Lock lock)
    {
        IOUtil.close(indexIn);

        active = false;
    }

    /**
     * Helper method so we can quickly activate ourselves on access
     */
    protected final void checkActive()
    {
        if (!active)
        {
            Activator.activate(this);
        }
    }

    /**
     * Read unparsed data for a given verse
     */
    public String getText(Verse verse) throws BookException
    {
        checkActive();

        try
        {
            // Seek to the correct point
            long location = indexArr[verse.getOrdinal() - 1];
            if (location == -1)
            {
                throw new BookException(Msg.READ_ERROR);
            }

            dataRaf.seek(location);

            // Read the XML text
            String txt = dataRaf.readUTF();
            return txt;
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.READ_ERROR, ex);
        }
    }

    /**
     * Write unparsed data for a given verse
     */
    public void setRawText(Key key, String text) throws BookException
    {
        checkActive();

        Verse verse = KeyUtil.getVerse(key);

        if (verse == null)
        {
            throw new BookException(Msg.WRITE_ERROR);
        }

        try
        {
            // Remember where we were so we can read it back later
            indexArr[verse.getOrdinal() - 1] = dataRaf.getFilePointer();
    
            // And write the entry
            dataRaf.writeUTF(text);
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.READ_ERROR, ex);
        }
    }

    /**
     * Flush the data written to disk
     */
    public void flush() throws BookException
    {
        checkActive();

        try
        {
            // re-open the RAF read-write
            dataRaf = new RandomAccessFile(NetUtil.getAsFile(dataUri), FileUtil.MODE_WRITE);

            // Save the ascii XML index
            PrintWriter indexOut = new PrintWriter(NetUtil.getOutputStream(indexUri));
            for (int i = 0; i < indexArr.length; i++)
            {
                indexOut.println(indexArr[i]);
            }

            indexOut.close();
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.WRITE_ERROR, ex);
        }
    }

    /**
     * Index filename
     */
    private static final String FILE_INDEX = "xml.index"; //$NON-NLS-1$

    /**
     * Data filename
     */
    private static final String FILE_DATA = "xml.data"; //$NON-NLS-1$

    /**
     * Are we active
     */
    private boolean active = false;

    /**
     * The directory in which the cache is stored
     */
    private URI uri;

    /**
     * URL of the index file
     */
    private URI indexUri;

    /**
     * URL of the text file
     */
    private URI dataUri;

    /**
     * The text random access file
     */
    private RandomAccessFile dataRaf;

    /**
     * The index file reader
     */
    private BufferedReader indexIn;

    /**
     * The hash of indexes into the text file, one per verse. Note that the
     * index in use is NOT the ordinal number of the verse since ordinal nos are
     * 1 based. The index into xml_arr is verse.getOrdinal() - 1
     */
    private long[] indexArr;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(BookDataCache.class);
}
