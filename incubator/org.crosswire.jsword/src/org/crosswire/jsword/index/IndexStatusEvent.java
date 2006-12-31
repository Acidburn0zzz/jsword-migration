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
 * ID: $Id: BooksEvent.java 763 2005-07-27 23:26:43Z dmsmith $
 */
package org.crosswire.jsword.index;

import java.util.EventObject;


/**
 * An IndexStatusEvent is fired whenever the IndexStatus
 * of a book has changed.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class IndexStatusEvent extends EventObject
{
    /**
     * Basic constructor
     * @param status The new status of the book.
     */
    public IndexStatusEvent(Object source, IndexStatus status)
    {
        super(source);

        indexStatus = status;
    }

    /**
     * @return Returns the indexStatus.
     */
    public IndexStatus getIndexStatus()
    {
        return indexStatus;
    }

    /**
     * The indexStatus of the book.
     */
    private IndexStatus indexStatus;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3834876879554819894L;

}
