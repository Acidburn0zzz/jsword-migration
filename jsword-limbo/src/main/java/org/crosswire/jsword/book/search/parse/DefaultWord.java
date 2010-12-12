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
 * The Search Word for a Word to search for. This is the default if no other
 * Words match.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class DefaultWord implements ParamWord, CommandWord {
    /**
     * Create a the default rule with the (presumably) Bible word that formed
     * part of the original search string
     * 
     * @param text
     *            The word to search (or otherwise) for
     */
    public DefaultWord(String text) {
        this.text = text;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.search.parse.ParamWord#getWord(org.crosswire
     * .jsword.book.search.parse.IndexSearcher)
     */
    public String getWord(IndexSearcher engine) {
        return text;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return text;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.search.parse.ParamWord#getPassage(org.crosswire
     * .jsword.book.search.parse.IndexSearcher)
     */
    public Key getKeyList(IndexSearcher engine) throws BookException {
        return engine.wordSearch(text);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.search.parse.CommandWord#updatePassage(org.
     * crosswire.jsword.book.search.parse.Searcher,
     * org.crosswire.jsword.passage.Passage)
     */
    public void updatePassage(IndexSearcher engine, Key key) throws BookException {
        // We need to have DefaultWord pretend to be a CommandWord so that
        // seearches like "moses aaron" work. DefaultWord(moses) has to be a
        // command for DefaultWord(aaron)
        // So if the stack is empty we need to pretend that the search had been
        // done using us as a word.
        if (engine.iterator().hasNext()) {
            key.retainAll(engine.iteratePassage());
        } else {
            key.retainAll(engine.wordSearch(text));
        }
    }

    /**
     * The word that we represent
     */
    private String text;
}
