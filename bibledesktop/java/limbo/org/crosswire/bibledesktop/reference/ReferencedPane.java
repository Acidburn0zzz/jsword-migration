package org.crosswire.bibledesktop.reference;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.crosswire.bibledesktop.book.BookListCellRenderer;
import org.crosswire.bibledesktop.book.BooksComboBoxModel;
import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.BookDataDisplayFactory;
import org.crosswire.bibledesktop.passage.KeyTreeNode;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;

/**
 * Builds a panel on which all the Dictionaries and their entries are visible.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class ReferencedPane extends JPanel implements BookDataDisplay
{
    /**
     * Simple ctor
     */
    public ReferencedPane()
    {
        filter = BookFilters.getAll();
        init();
    }

    /**
     * Ctor that filters the books displayed
     */
    public ReferencedPane(BookFilter filter)
    {
        this.filter = filter;
        init();
    }

    /**
     * GUI initialiser
     */
    private void init()
    {
        mdlBooks.setFilter(filter);
        tblBooks.setVisibleRowCount(4);
        tblBooks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBooks.setModel(mdlBooks);
        tblBooks.setCellRenderer(new BookListCellRenderer());
        tblBooks.setPrototypeCellValue(BookListCellRenderer.PROTOTYPE_BOOK_NAME);
        tblBooks.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                if (ev.getValueIsAdjusting())
                {
                    return;
                }

                newBook();
            }
        });
        scrBooks.setViewportView(tblBooks);

        //treEntries.setCellRenderer(new KeyTreeCellRenderer());
        //treEntries.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
        treEntries.setModel(new ReferenceTreeModel());
        treEntries.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent ev)
            {
                newEntry();
            }
        });
        scrEntries.setViewportView(treEntries);

        scrDisplay.setViewportView(txtDisplay.getComponent());

        sptMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
        sptMain.setTopComponent(scrEntries);
        sptMain.setBottomComponent(scrDisplay);

        this.setLayout(new BorderLayout(5, 5));
        this.add(scrBooks, BorderLayout.NORTH);
        this.add(sptMain, BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.Book, org.crosswire.jsword.passage.Key)
     */
    public void setBookData(Book book, Key key)
    {
        throw new UnsupportedOperationException(); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.FocusablePart#getComponent()
     */
    public Component getComponent()
    {
        return this;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#copy()
     */
    public void copy()
    {
        txtDisplay.copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#getKey()
     */
    public Key getKey()
    {
        TreePath path = treEntries.getSelectionPath();
        if (path == null)
        {
            return null;
        }

        KeyTreeNode keytn = (KeyTreeNode) path.getLastPathComponent();
        return keytn.getKey();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.FocusablePart#getBook()
     */
    public Book getBook()
    {
        return book;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#addHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void addHyperlinkListener(HyperlinkListener li)
    {
        txtDisplay.addHyperlinkListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void removeHyperlinkListener(HyperlinkListener li)
    {
        txtDisplay.removeHyperlinkListener(li);
    }

    /**
     * See if the current book has a mention of the word in question.
     * LATER(joe): add a background task to highlight other dictionaries that have the word.
     */
    public void setWord(String data)
    {
        try
        {
            if (book == null)
            {
                return;
            }

            Key key = book.getKey(data);
            if (key != null)
            {
                List lpath = new ArrayList();
                while (true)
                {
                    lpath.add(0, key);
                    key = key.getParent();

                    if (key == null)
                    {
                        break;
                    }
                }
                Key[] keys = (Key[]) lpath.toArray(new Key[lpath.size()]);

                TreePath path = new TreePath(keys);
                treEntries.setSelectionPath(path);
            }
        }
        catch (NoSuchKeyException ex)
        {
            // ignore
        }
    }

    /*
        // Code to search for a word
        for (Iterator it = Books.getBooks(filter).iterator(); it.hasNext();)
        {
            BookMetaData dmd = (BookMetaData) it.next();
            Book tempdict = dmd.getBook();
            try
            {
                Key key = tempdict.getKey(data);
                lstdicts.setSelectedValue(tempdict, true);
                lstentries.setSelectedValue(key, true);
                return;
            }
            catch (BookException ex)
            {
                // ignore - we only wanted to see if it could be done.
            }
        }     
    */

    /**
     * Called when someone selects a new Book
     */
    protected void newBook()
    {
        Object selected = tblBooks.getSelectedValue();
        BookMetaData bmd = (BookMetaData) selected;
        book = bmd.getBook();
        Key set = book.getGlobalKeyList();

        TreeModel model = new DefaultTreeModel(new KeyTreeNode(set, null), true);
        treEntries.setModel(model);
    }

    /**
     * Called when someone selects a new entry from the current book
     */
    protected void newEntry()
    {
        try
        {
            Key key = getKey();
            if (key != null)
            {
                txtDisplay.setBookData(book, key);
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * The display of OSIS data
     */
    private BookDataDisplay txtDisplay = BookDataDisplayFactory.createBookDataDisplay();

    /*
     * Gui components
     */
    private BookFilter filter = null;
    private BooksComboBoxModel mdlBooks = new BooksComboBoxModel();
    private Book book = null;
    private JScrollPane scrBooks = new JScrollPane();
    private JList tblBooks = new JList();
    private JSplitPane sptMain = new JSplitPane();
    private JScrollPane scrEntries = new JScrollPane();
    private JScrollPane scrDisplay =new JScrollPane();
    private JTree treEntries = new JTree();
}