/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
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
package org.crosswire.jsword.book.search.lucene;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.IndexStatus;
import org.crosswire.jsword.book.search.Index;
import org.crosswire.jsword.book.search.IndexManager;
import org.crosswire.jsword.util.Project;

/**
 * An implementation of IndexManager for Lucene indexes.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class LuceneIndexManager implements IndexManager
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.AbstractIndex#isIndexed()
     */
    public boolean isIndexed(Book book)
    {
        try
        {
            URL storage = getStorageArea(book);
            URL longer = NetUtil.lengthenURL(storage, DIR_SEGMENTS);
            return NetUtil.isFile(longer);
        }
        catch (IOException ex)
        {
            log.error("Failed to find lucene index storage area.", ex); //$NON-NLS-1$
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.IndexManager#getIndex(org.crosswire.jsword.book.Book)
     */
    public Index getIndex(Book book) throws BookException
    {
        try
        {
            Index reply = (Index) INDEXES.get(book);
            if (reply == null)
            {
                URL storage = getStorageArea(book);
                reply = new LuceneIndex(book, storage);
                INDEXES.put(book, reply);
            }

            return reply;
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.LUCENE_INIT, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.AbstractIndex#generateSearchIndex(org.crosswire.common.progress.Job)
     */
    public void scheduleIndexCreation(final Book book)
    {
        book.setIndexStatus(IndexStatus.SCHEDULED);

        Thread work = new Thread(new Runnable()
        {
            public void run()
            {
                IndexStatus finalStatus = IndexStatus.UNDONE;
                URL storage = null;
                try
                {
                    storage = getStorageArea(book);
                    Index index = new LuceneIndex(book, storage, true);
                    // We were successful if the directory exists.
                    if (NetUtil.getAsFile(storage).exists())
                    {
                        finalStatus = IndexStatus.DONE;
                        INDEXES.put(book, index);
                    }
                }
                catch (Exception ex)
                {
                    Reporter.informUser(LuceneIndexManager.this, ex);
                }
                finally
                {
                    book.setIndexStatus(finalStatus);
                }
            }
        });
        work.start();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.IndexManager#installDownloadedIndex(org.crosswire.jsword.book.Book, java.net.URL)
     */
    public void installDownloadedIndex(Book book, URL tempDest) throws BookException
    {
        try
        {
            URL storage = getStorageArea(book);
            File zip = NetUtil.getAsFile(tempDest);
            IOUtil.unpackZip(zip, NetUtil.getAsFile(storage));
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.INSTALL_FAIL, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.IndexManager#deleteIndex(org.crosswire.jsword.book.Book)
     */
    public void deleteIndex(Book book) throws BookException
    {
        // Lucene can build in the directory that currently exists,
        // overwriting what is there. So we rename the directory,
        // mark the operation as success and then try to delete the
        // directory.
        File tempPath = null;
        try
        {
            // TODO(joe): This needs some checks that it isn't being used
            File storage = NetUtil.getAsFile(getStorageArea(book));
            String finalCanonicalPath = storage.getCanonicalPath();
            tempPath = new File(finalCanonicalPath + '.' + IndexStatus.CREATING.toString());
            FileUtil.delete(tempPath);
            storage.renameTo(tempPath);
            book.setIndexStatus(IndexStatus.UNDONE);
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.DELETE_FAILED, ex);
        }

        FileUtil.delete(tempPath);
    }

    /**
     * Determine where an index should be stored
     * @param book The book to be indexed
     * @return A URL to store stuff in
     * @throws IOException If there is a problem in finding where to store stuff
     */
    protected URL getStorageArea(Book book) throws IOException
    {
        BookMetaData bmd = book.getBookMetaData();
        String driverName = bmd.getDriverName();
        String bookName = bmd.getInitials();

        assert driverName != null;
        assert bookName != null;

        URL base = Project.instance().getTempScratchSpace(DIR_LUCENE, false);
        URL driver = NetUtil.lengthenURL(base, driverName);

        return NetUtil.lengthenURL(driver, bookName);
    }

    /**
     * The created indexes
     */
    protected static final Map INDEXES = new HashMap();

    /**
     * The segments directory
     */
    private static final String DIR_SEGMENTS = "segments"; //$NON-NLS-1$

    /**
     * The lucene search index directory
     */
    private static final String DIR_LUCENE = "lucene"; //$NON-NLS-1$

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(LuceneIndexManager.class);
}
