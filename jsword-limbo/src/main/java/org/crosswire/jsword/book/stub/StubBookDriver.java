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
package org.crosswire.jsword.book.stub;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.basic.AbstractBookDriver;

/**
 * StubBookDriver is a simple stub implementation of BibleDriver that is pretty
 * much always going to work because it has no dependancies on external files.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class StubBookDriver extends AbstractBookDriver {
    /**
     * Setup the array of BookMetaDatas
     */
    public StubBookDriver() {
        books = new Book[] {
                new StubBook(this, "Stub Version", BookCategory.BIBLE),
                new StubBook(this, "New Stub Version", BookCategory.BIBLE),
                new StubBook(this, "Stub Comments", BookCategory.COMMENTARY),
                new StubDictionary(this, "Stub Dict", BookCategory.DICTIONARY),
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookDriver#getBooks()
     */
    public Book[] getBooks() {
        return books;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookDriver#getDriverName()
     */
    public String getDriverName() {
        return "Stub";
    }

    /**
     * The meta data array
     */
    private Book[] books;
}
