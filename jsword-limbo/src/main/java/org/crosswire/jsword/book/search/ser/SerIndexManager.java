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
package org.crosswire.jsword.book.search.ser;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.IOUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.index.Index;
import org.crosswire.jsword.index.IndexManager;

/**
 * An implementation of IndexManager that controls Ser indexes.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class SerIndexManager implements IndexManager {
    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.search.AbstractIndex#isIndexed()
     */
    public boolean isIndexed(Book book) {
        try {
            URI storage = getStorageArea(book);
            URI longer = NetUtil.lengthenURI(storage, SerIndex.FILE_INDEX);
            return NetUtil.isFile(longer);
        } catch (IOException ex) {
            log.error("Failed to find lucene index storage area.", ex);
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.search.IndexManager#getIndex(org.crosswire.
     * jsword.book.Book)
     */
    public Index getIndex(Book book) throws BookException {
        try {
            Index reply = (Index) indexes.get(book);
            if (reply == null) {
                URI storage = getStorageArea(book);
                reply = new SerIndex(book, storage);
                indexes.put(book, reply);
            }

            return reply;
        } catch (IOException ex) {
            throw new BookException(Msg.SER_INIT, ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.search.AbstractIndex#generateSearchIndex(org
     * .crosswire.common.progress.Job)
     */
    public void scheduleIndexCreation(final Book book) {
        Thread work = new Thread(new Runnable() {
            public void run() {
                try {
                    URI storage = getStorageArea(book);
                    Index index = new SerIndex(book, storage, true);
                    indexes.put(book, index);
                } catch (Exception ex) {
                    Reporter.informUser(SerIndexManager.this, ex);
                }
            }
        });
        work.start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.search.IndexManager#installDownloadedIndex(
     * org.crosswire.jsword.book.Book, java.net.URL)
     */
    public void installDownloadedIndex(Book book, URI tempDest) throws BookException {
        try {
            URI storage = getStorageArea(book);
            File zip = NetUtil.getAsFile(tempDest);
            IOUtil.unpackZip(zip, NetUtil.getAsFile(storage));
        } catch (IOException ex) {
            throw new BookException(Msg.INSTALL_FAIL, ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.search.IndexManager#deleteIndex(org.crosswire
     * .jsword.book.Book)
     */
    public void deleteIndex(Book book) throws BookException {
        try {
            // TODO(joe): This needs some checks that it isn't being used
            URI storage = getStorageArea(book);
            NetUtil.delete(storage);
        } catch (IOException ex) {
            throw new BookException(Msg.DELETE_FAILED, ex);
        }
    }

    /**
     * Determine where an index should be stored
     * 
     * @param book
     *            The book to be indexed
     * @return A URL to store stuff in
     * @throws IOException
     *             If there is a problem in finding where to store stuff
     */
    protected URI getStorageArea(Book book) throws IOException {
        BookMetaData bmd = book.getBookMetaData();
        String driverName = bmd.getDriverName();
        String bookName = bmd.getInitials();

        assert driverName != null;
        assert bookName != null;

        URI base = CWProject.instance().getWriteableProjectSubdir(DIR_SER, false);
        URI driver = NetUtil.lengthenURI(base, driverName);

        return NetUtil.lengthenURI(driver, bookName);
    }

    /**
     * The created indexes
     */
    protected static final Map indexes = new HashMap();

    /**
     * The ser search index directory
     */
    private static final String DIR_SER = "ser";

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SerIndexManager.class);
}
