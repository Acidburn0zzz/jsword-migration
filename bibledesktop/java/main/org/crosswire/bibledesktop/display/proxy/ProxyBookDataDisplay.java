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
package org.crosswire.bibledesktop.display.proxy;

import java.awt.Component;

import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.URLEventListener;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;

/**
 * An implementation of BookDataDisplay that simply proxies all requests to an
 * underlying BookDataDisplay.
 * <p>Useful for chaining a few BookDataDisplays together to add functionallity
 * component by component.</p>
 *
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ProxyBookDataDisplay implements BookDataDisplay
{
    /**
     * Setup the proxy
     */
    public ProxyBookDataDisplay(BookDataDisplay proxy)
    {
        this.proxy = proxy;
    }

    /**
     * Accessor for the proxy
     * @return Returns the proxy.
     */
    protected BookDataDisplay getProxy()
    {
        return proxy;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#addHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void addURLEventListener(URLEventListener listener)
    {
        proxy.addURLEventListener(listener);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void removeURLEventListener(URLEventListener listener)
    {
        proxy.removeURLEventListener(listener);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#copy()
     */
    public void copy()
    {
        proxy.copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getComponent()
     */
    public Component getComponent()
    {
        return proxy.getComponent();
    }

    /*
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#setBookData(Book, Key)
     */
    public void setBookData(Book book, Key key)
    {
        proxy.setBookData(book, key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#refresh()
     */
    public void refresh()
    {
        proxy.refresh();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return proxy.toString();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getKey()
     */
    public Key getKey()
    {
        return getProxy().getKey();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getBook()
     */
    public Book getBook()
    {
        return getProxy().getBook();
    }

    /**
     * The component to which we proxy
     */
    private BookDataDisplay proxy;
}
