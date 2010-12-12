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
package org.crosswire.jsword.book.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.basic.AbstractPassageBook;
import org.crosswire.jsword.book.basic.DefaultBookMetaData;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.filter.FilterFactory;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;

/**
 * JDBCBook implements Bible, and gets the text from a JDBC database.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class JDBCBook extends AbstractPassageBook {
    /**
     * Simple ctor
     */
    public JDBCBook(BookDriver driver, Map<String,Object> prop) throws BookException {
        super(null); // set BookMetaData later

        setBookMetaData(new DefaultBookMetaData(driver, this, prop));

        // Load the specified JDBC name
        int driver_attempt = 1;
        while (true) {
            String property = "JdbcDriver" + driver_attempt;
            String drivername = getProperty(property).toString();

            try {
                Class.forName(drivername);
                break;
            } catch (Exception ex) {
                log.debug("Failed to load JDBC name: " + driver + " (System Message: " + ex + ")");
            }

            driver_attempt++;
        }

        try {
            // Actually connect to the database
            String text_url = getProperty("TextURL").toString();
            textCon = DriverManager.getConnection(text_url);

            String concord_url = getProperty("ConcordURL").toString();
            concCon = DriverManager.getConnection(concord_url);

            // SQL statements
            String doc_query = getProperty("DocQuery").toString();
            docStmt = textCon.prepareStatement(doc_query);

            String ref_query = getProperty("RefQuery").toString();
            refStmt = concCon.prepareStatement(ref_query);

            // String verse_query = getProperty("VerseQuery");
            // verse_stmt = textcnx.prepareStatement(verse_query);

            String start_query = getProperty("StartQuery").toString();
            startStmt = concCon.prepareStatement(start_query);

            wordsQuery = getProperty("WordsQuery").toString();
        } catch (SQLException ex) {
            textCon = null;
            concCon = null;
            docStmt = null;
            refStmt = null;
            // verse_stmt = null;
            startStmt = null;
            wordsQuery = null;

            throw new BookException(Msg.BIBLE_CONNECT, ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#getFilter()
     */
    protected Filter getFilter() {
        return FilterFactory.getDefaultFilter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.Book#contains(org.crosswire.jsword.passage.Key)
     */
    public boolean contains(Key key) {
        return getRawText(key) != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.basic.AbstractPassageBook#getRawText(org.crosswire
     * .jsword.passage.Key)
     */
    public String getRawText(Key key) {
        String reply = "";
        ResultSet rs = null;

        Verse verse = KeyUtil.getVerse(key);

        try {
            docStmt.setInt(1, verse.getOrdinal());
            docStmt.setInt(2, verse.getOrdinal());
            rs = docStmt.executeQuery();

            while (rs.next()) {
                String vtext = rs.getString(5);

                // If the verse is empty then we shouldn't add the verse tag
                if (vtext != null && vtext.length() > 0) {
                    reply = JDBCBibleUtil.processText(vtext);
                }
            }
        } catch (SQLException ex) {
            log.fatal("read failed", ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                log.fatal("close() failed", ex);
            }
        }

        return reply;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.basic.AbstractPassageBook#setRawText(org.crosswire
     * .jsword.passage.Key, java.lang.String)
     */
    public void setRawText(Key key, String rawData) throws BookException {
        throw new BookException(Msg.DRIVER_READONLY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.Book#setAliasKey(org.crosswire.jsword.passage
     * .Key, org.crosswire.jsword.passage.Key)
     */
    public void setAliasKey(Key alias, Key source) throws BookException {
        throw new BookException(Msg.DRIVER_READONLY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.search.Index#findWord(java.lang.String)
     */
    public Key findWord(String word) throws BookException {
        if (word == null) {
            return createEmptyKeyList();
        }

        word = JDBCBibleUtil.swapChar(word, '-', '?').toLowerCase();

        try {
            Key retcode = createEmptyKeyList();

            refStmt.setString(1, word);
            ResultSet rs = refStmt.executeQuery();
            while (rs.next()) {
                Verse temp = new Verse(rs.getInt(1), rs.getInt(2), rs.getInt(3));
                retcode.addAll(temp);
            }

            rs.close();

            return retcode;
        } catch (NoSuchVerseException ex) {
            log.error("word=" + word);
            assert false : ex;
            return createEmptyKeyList();
        } catch (SQLException ex) {
            log.error("word=" + word);
            throw new BookException(Msg.BIBLE_DB, ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.search.Index#getStartsWith(java.lang.String)
     */
    public Collection getStartsWith(String word) throws BookException {
        try {
            ArrayList output = new ArrayList();

            // word = JDBCBibleUtil.swapChar(word, '\'', '?');
            startStmt.setString(1, word + "%");
            ResultSet rs = startStmt.executeQuery();
            while (rs.next()) {
                output.add(rs.getString(1));
            }

            rs.close();

            return output;
        } catch (SQLException ex) {
            log.error("word=" + word);
            throw new BookException(Msg.BIBLE_DB, ex);
        }
    }

    /*
     * Where does this verse come in the Bible. Starting with Gen 1:1 as number
     * 1 counting up one per verse and not resetting at each new chapter.
     * 
     * @param book The book part of the reference.
     * 
     * @param chapter The current chapter
     * 
     * @param verse The current verse
     * 
     * @return The ordinal number of verses
     * 
     * @exception NoSuchVerseException If the reference is illegal
     * 
     * private int verseOrdinal(int book, int chapter, int verse) throws
     * NoSuchVerseException, SQLException { int retcode = 0;
     * 
     * verse_stmt.setInt(1, book); verse_stmt.setInt(2, chapter);
     * verse_stmt.setInt(3, verse); ResultSet rs = verse_stmt.executeQuery();
     * 
     * if (!rs.next()) { throw new NoSuchVerseException(Msg.BIBLE_LOST); }
     * 
     * retcode = rs.getInt(1);
     * 
     * rs.close();
     * 
     * return retcode; }
     */

    /**
     * Cached statement for getDocument
     */
    private PreparedStatement docStmt;

    /**
     * Cached statement for findWord
     */
    private PreparedStatement refStmt;

    /**
     * Cached statement for verseOrdinal
     * 
     * private PreparedStatement verse_stmt;
     * 
     * /** Cached statement for startsWith
     */
    private PreparedStatement startStmt;

    /**
     * The statment for this is part of the enumeration
     */
    protected String wordsQuery;

    /**
     * The conenction to the text data source
     */
    private Connection textCon;

    /**
     * The conenction to the concordance
     */
    protected Connection concCon;

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(JDBCBook.class);

    /**
     * Helper class to enumerate through the words in a version
     */
    static class WordIterator implements Iterator {
        /**
         * Create the necessary SQL query
         */
        protected WordIterator(Statement stmt, ResultSet rs) throws BookException {
            this.stmt = stmt;
            this.rs = rs;

            moveToNext();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return more;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Iterator#next()
         */
        public Object next() throws NoSuchElementException {
            try {
                String retcode = rs.getString(1);
                moveToNext();

                // If we got a null then have one more go ...
                if (retcode == null) {
                    retcode = rs.getString(1);
                    moveToNext();
                }

                return retcode;
            } catch (Exception ex) {
                log.warn("SQL error in iteration", ex);
                throw new NoSuchElementException(ex.getMessage());
            }
        }

        /**
         * Not supported
         * 
         * @throws UnsupportedOperationException
         *             Every time ...
         */
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        /**
         * Check for more. If there are none, shut up shop to be more resource
         * friendly
         */
        private void moveToNext() throws BookException {
            try {
                more = rs.next();

                if (!more) {
                    rs.close();
                    stmt.close();
                }
            } catch (SQLException ex) {
                throw new BookException(Msg.BIBLE_DB, ex);
            }
        }

        /**
         * Are there any more words?
         */
        private boolean more = true;

        /**
         * The local connection
         */
        private Statement stmt = null;

        /**
         * The result from the database
         */
        private ResultSet rs = null;
    }
}
