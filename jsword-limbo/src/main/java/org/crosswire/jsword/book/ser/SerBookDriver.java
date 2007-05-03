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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.crosswire.common.util.CollectionUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.basic.AbstractBookDriver;
import org.crosswire.jsword.book.basic.BookRoot;
import org.crosswire.jsword.book.raw.RawBook;

/**
 * This represents all of the SerBibles.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class SerBookDriver extends AbstractBookDriver
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getBooks()
     */
    public Book[] getBooks()
    {
        try
        {
            URI dir = BookRoot.findBibleRoot(getDriverName());

            if (!NetUtil.isDirectory(dir))
            {
                log.debug("Missing ser directory: " + dir); //$NON-NLS-1$
                return new Book[0];
            }

            String[] names = null;
            if (dir == null)
            {
                names = new String[0];
            }
            else
            {
                names = NetUtil.list(dir, new NetUtil.IsDirectoryURIFilter(dir));
            }

            List books = new ArrayList();

            for (int i=0; i<names.length; i++)
            {
                URI uri = NetUtil.lengthenURI(dir, names[i]);
                URI propUri = NetUtil.lengthenURI(uri, "bible.properties"); //$NON-NLS-1$

                Book book = new RawBook(this, CollectionUtil.properties2Map(propUri), uri);

                books.add(book);
            }

            return (Book[]) books.toArray(new Book[books.size()]);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
            return new Book[0];
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getDriverName()
     */
    public String getDriverName()
    {
        return "ser"; //$NON-NLS-1$
    }

    /**
     * The log stream
     */
    private static Logger log = Logger.getLogger(SerBookDriver.class);
}
