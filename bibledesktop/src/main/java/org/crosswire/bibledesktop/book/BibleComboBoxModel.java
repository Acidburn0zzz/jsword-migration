/**
 * Distribution License:
 * BibleDesktop is free software; you can redistribute it and/or modify it under
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
package org.crosswire.bibledesktop.book;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.Versification;
import org.crosswire.jsword.versification.system.Versifications;

/**
 * A ComboBoxModel for selecting book/chapter/verse.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
/**
 *
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class BibleComboBoxModel extends AbstractListModel implements ComboBoxModel {
    /**
     * The level of the book combo.
     */
    protected enum Level {
        /**
         * For when the we are a book level combo
         */
        BOOK,

        /**
         * For when the we are a chapter level combo
         */
        CHAPTER,

        /**
         * For when the we are a verse level combo
         */
        VERSE,
    }

    /**
     * Simple ctor for choosing verses
     */
    protected BibleComboBoxModel(BibleComboBoxModelSet set, Level level) {
        this.set = set;
        this.level = level;

        switch (level) {
        case BOOK:
            selected = set.getVerse().getBook().getBookName();
            break;

        case CHAPTER:
            selected = Integer.valueOf(set.getVerse().getChapter());
            break;

        case VERSE:
            selected = Integer.valueOf(set.getVerse().getVerse());
            break;

        default:
            assert false : level;
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
     */
    public void setSelectedItem(Object selected) {
        log.debug("setSelectedItem(" + selected + ") level=" + level);

        switch (level) {
        case BOOK:
            BibleBook book = (BibleBook) selected;
            assert book != null;
            setBook(book);
            break;

        case CHAPTER:
            Integer csel = (Integer) selected;
            setChapter(csel.intValue());
            break;

        case VERSE:
            Integer vsel = (Integer) selected;
            setVerse(vsel.intValue());
            break;

        default:
            assert false : level;
        }

        this.selected = selected;
    }

    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#getSelectedItem()
     */
    public Object getSelectedItem() {
        return selected;
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        switch (level) {
        case BOOK:
            return v11n.getBooks().getBookCount();

        case CHAPTER:
            return v11n.getLastChapter(set.getVerse().getBook());

        case VERSE:
            return v11n.getLastVerse(set.getVerse().getBook(), set.getVerse().getChapter());

        default:
            assert false : level;
            return 0;
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index) {
        switch (level) {
        case BOOK:
            return v11n.getBooks().getBook(index);
        default:
            return Integer.valueOf(index + 1);

        }
    }

    /**
     * Accessor for the book
     */
    public void setBook(BibleBook book) {
        // Try to honor current chapter and verse
        // Use 1 if it is not possible
        Verse old = set.getVerse();
        int chapter = old.getChapter();
        int verse = old.getVerse();

        chapter = Math.min(chapter, v11n.getLastChapter(book));
        verse = Math.min(verse, v11n.getLastVerse(book, chapter));

        Verse update = new Verse(book, chapter, verse);
        set.setVerse(update);
    }

    /**
     * Accessor for the chapter
     */
    public void setChapter(int chapter) {
        // Try to honor current verse
        // Use 1 if it is not possible
        Verse old = set.getVerse();
        BibleBook book = old.getBook();
        int verse = old.getVerse();

        verse = Math.min(verse, v11n.getLastVerse(book, chapter));

        Verse update = new Verse(book, chapter, verse);
        set.setVerse(update);
    }

    /**
     * Accessor for the chapter
     */
    public void setVerse(int verse) {
        Verse old = set.getVerse();
        Verse update = new Verse(old.getBook(), old.getChapter(), verse);
        set.setVerse(update);
    }

    /* (non-Javadoc)
     * @see javax.swing.AbstractListModel#fireContentsChanged(java.lang.Object, int, int)
     */
    @Override
    protected void fireContentsChanged(Object source, int index0, int index1) {
        super.fireContentsChanged(source, index0, index1);
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        // Broken but we don't serialize views
        set = null;
        selected = null;
        // AV11N(DMS): Is this right?
        v11n = Versifications.instance().getDefaultVersification();
        is.defaultReadObject();
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(BibleComboBoxModel.class);

    // AV11N(DMS): Is this right?
    private transient Versification v11n = Versifications.instance().getDefaultVersification();

    /**
     * Shared settings
     */
    private transient BibleComboBoxModelSet set;

    /**
     * What is currently selected?
     */
    private transient Object selected;

    /**
     * Are we a book, chapter or verse selector
     */
    protected Level level;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3616449020667442997L;
}
