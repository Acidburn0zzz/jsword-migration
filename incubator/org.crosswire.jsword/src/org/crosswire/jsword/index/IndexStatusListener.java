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
 * ID: $Id: BooksListener.java 763 2005-07-27 23:26:43Z dmsmith $
 */
package org.crosswire.jsword.index;

import java.util.EventListener;


/**
 * IndexStatusListeners are able to be notified about changes to the
 * IndexStatus of a book.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public interface IndexStatusListener extends EventListener
{
    /**
     * Called whenever the IndexStatus of a book has changed.
     * @param ev A description of the change
     */
    void statusChanged(IndexStatusEvent ev);
}
