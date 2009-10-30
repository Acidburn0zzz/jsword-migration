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
package org.crosswire.jsword.book.search.parse;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;

/**
 * Alter the Passage by calling removeAll with a Passage grabbed from the next
 * word in the search string.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class RemoveCommandWord implements CommandWord {
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.search.parse.CommandWord#updatePassage(org.
     * crosswire.jsword.book.search.parse.Searcher,
     * org.crosswire.jsword.passage.Passage)
     */
    public void updatePassage(IndexSearcher engine, Key key) throws BookException {
        key.removeAll(engine.iteratePassage());
    }
}
