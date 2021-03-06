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
package org.crosswire.jsword.book.raw;

/**
 * Various constants for RawBooks.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class RawConstants {
    static final String SIG_PARA_INST = "RAW:AI";
    static final String SIG_PUNC_INST = "RAW:PI";
    static final String SIG_CASE_INST = "RAW:CI";
    static final String SIG_WORD_INST = "RAW:WI";

    static final String SIG_PUNC_ITEM = "RAW:PR";
    static final String SIG_WORD_ITEM = "RAW:WR";

    static final String FILE_PARA_INST = "parainst.idx";
    static final String FILE_PUNC_INST = "puncinst.idx";
    static final String FILE_CASE_INST = "caseinst.idx";
    static final String FILE_WORD_INST = "wordinst.idx";

    static final String FILE_PUNC_ITEM = "punc.idx";
    static final String FILE_WORD_ITEM = "word.idx";

    static final String FILE_BIBLE_PROPERTIES = "bible.properties";

    /**
     * Prevent instantiation
     */
    private RawConstants() {
    }
}
