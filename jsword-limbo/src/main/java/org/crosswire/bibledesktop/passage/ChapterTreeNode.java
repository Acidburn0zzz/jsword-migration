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
package org.crosswire.bibledesktop.passage;

import java.util.Iterator;

import javax.swing.tree.TreeNode;

import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.BibleInfo;

/**
 * PassageTableModel.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ChapterTreeNode extends BookTreeNode {
    /**
     * This constructor is for when we are really a BookTreeNode
     */
    protected ChapterTreeNode(TreeNode parent, int book, int chapter) throws NoSuchVerseException {
        super(parent, book);
        this.chapter = chapter;

        kids = new VerseTreeNode[BibleInfo.versesInChapter(book, chapter)];
    }

    /**
     * This constructor is for when we are really a BookTreeNode
     */
    public void setPassage(Passage ref, boolean filter) {
        this.ref = ref;

        if (filter) {
            try {
                kids = new VerseTreeNode[ref.versesInPassage(book, chapter)];

                int verse_count = 0;

                Iterator it = ref.iterator();
                while (it.hasNext()) {
                    Verse verse = (Verse) it.next();

                    if ((book == 0 || verse.getBook() == book) && (chapter == 0 || verse.getChapter() == chapter)) {
                        VerseTreeNode node = new VerseTreeNode(this, book, chapter, verse.getVerse());
                        node.setPassage(ref, true);
                        kids[verse_count++] = node;
                    }
                }
            } catch (NoSuchVerseException ex) {
                assert false : ex;
            }
        }
    }

    /**
     * Returns the child <code>TreeNode</code> at index i
     */
    public TreeNode getChildAt(int i) {
        try {
            if (kids[i] != null) {
                return kids[i];
            }

            kids[i] = new VerseTreeNode(this, book, chapter, i + 1);
            return kids[i];
        } catch (NoSuchVerseException ex) {
            assert false : ex;
            return null;
        }
    }

    /**
     * Returns the index of <code>node</code> in the receivers children. If the
     * receiver does not contain <code>node</code>, -1 will be returned.
     */
    public int getIndex(TreeNode node) {
        if (!(node instanceof VerseTreeNode)) {
            return -1;
        }

        VerseTreeNode verse = (VerseTreeNode) node;
        return verse.getVerse();
    }

    /**
     * How we appear in the Tree
     */
    public String toString() {
        try {
            String chapNum = Integer.toString(chapter);
            if (ref == null) {
                return chapNum;
            }

            int verses = ref.versesInPassage(book, chapter);
            if (verses == 0) {
                return chapNum;
            }

            return chapNum + " (" + verses + ')';
        } catch (NoSuchVerseException ex) {
            assert false : ex;
            return "!Error!";
        }
    }

    /**
     * The current Passage number
     */
    public int getChapter() {
        return chapter;
    }

    /**
     * The Book that this node referrs to
     */
    protected int chapter;
}
