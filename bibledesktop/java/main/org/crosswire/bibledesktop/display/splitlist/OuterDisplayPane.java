package org.crosswire.bibledesktop.display.splitlist;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.bibledesktop.book.BibleViewPane;
import org.crosswire.bibledesktop.book.DisplaySelectEvent;
import org.crosswire.bibledesktop.book.DisplaySelectListener;
import org.crosswire.bibledesktop.book.DisplaySelectPane;
import org.crosswire.bibledesktop.display.FocusablePart;
import org.crosswire.bibledesktop.display.tab.TabbedDisplayPane;
import org.crosswire.bibledesktop.passage.PassageGuiUtil;
import org.crosswire.bibledesktop.passage.PassageListModel;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.VerseRange;

/**
 * A quick Swing Bible display pane.
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
public class OuterDisplayPane extends JPanel implements FocusablePart
{
    /**
     * Initialize the OuterDisplayPane
     */
    public OuterDisplayPane()
    {
        try
        {
            List booklist = Books.installed().getBookMetaDatas(BookFilters.getBibles());
            if (booklist.size() != 0)
            {
                Book book = ((BookMetaData) booklist.get(0)).getBook();
                txtPassg.setBook(book);
            }

            initialize();
        }
        catch (Exception ex)
        {
            log.error("Failed to set default book", ex); //$NON-NLS-1$
        }
    }

    /**
     * Create the GUI
     */
    private void initialize()
    {
        mdlPassg.setMode(PassageListModel.LIST_RANGES);
        mdlPassg.setRestriction(PassageConstants.RESTRICT_CHAPTER);

        lstPassg.setModel(mdlPassg);
        lstPassg.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                selection();
            }
        });

        sptPassg.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        sptPassg.add(scrPassg, JSplitPane.LEFT);
        sptPassg.add(txtPassg, JSplitPane.RIGHT);
        sptPassg.setOneTouchExpandable(true);
        sptPassg.setDividerLocation(0.0D);

        scrPassg.getViewport().add(lstPassg);

        this.setLayout(new BorderLayout());
        this.add(sptPassg, BorderLayout.CENTER);
    }

    /**
     * Accessor for a notifier from the DisplaySelectPane
     */
    public DisplaySelectListener getDisplaySelectListener()
    {
        return dsli;
    }

    /**
     * Set the passage to be displayed
     */
    public void setPassage(Passage ref)
    {
        this.ref = ref;

        try
        {
            mdlPassg.setPassage(ref);
            txtPassg.setPassage(ref);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Get the passage being displayed.
     */
    public Passage getPassage()
    {
        return mdlPassg.getPassage();
    }

    /**
     * Delete the selected verses
     */
    public void deleteSelected(BibleViewPane view)
    {
        PassageGuiUtil.deleteSelectedVersesFromList(lstPassg);

        // Update the text box
        ref = mdlPassg.getPassage();
        DisplaySelectPane psel = view.getSelectPane();
        psel.setPassage(ref);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#copy()
     */
    public void copy()
    {
        txtPassg.copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#addHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void addHyperlinkListener(HyperlinkListener li)
    {
        txtPassg.addHyperlinkListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void removeHyperlinkListener(HyperlinkListener li)
    {
        txtPassg.removeHyperlinkListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#getOSISSource()
     */
    public String getOSISSource()
    {
        return txtPassg.getOSISSource();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#getHTMLSource()
     */
    public String getHTMLSource()
    {
        return txtPassg.getHTMLSource();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#getKey()
     */
    public Key getKey()
    {
        return txtPassg.getKey();
    }

    /**
     * Someone clicked on a value in the list
     */
    protected void selection()
    {
        try
        {
            Object[] ranges = lstPassg.getSelectedValues();

            Passage local = null;
            if (ranges.length == 0)
            {
                local = ref;
            }
            else
            {
                local = PassageFactory.createPassage();
                for (int i=0; i<ranges.length; i++)
                {
                    local.add((VerseRange) ranges[i]);
                }

                // if there was a single selection then show the whole chapter
                if (ranges.length == 1)
                {
                    local.blur(1000, PassageConstants.RESTRICT_CHAPTER);
                }
            }

            txtPassg.setPassage(local);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * The whole passage that we are viewing
     */
    private Passage ref;

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(OuterDisplayPane.class);

    /*
     * GUI Components
     */
    private JSplitPane sptPassg = new JSplitPane();
    private JScrollPane scrPassg = new JScrollPane();
    protected TabbedDisplayPane txtPassg = new TabbedDisplayPane();
    private JList lstPassg = new JList();
    private PassageListModel mdlPassg = new PassageListModel();
    private DisplaySelectListener dsli = new CustomDisplaySelectListener();

    /**
     * Update the display whenever the version or passage changes
     */
    private class CustomDisplaySelectListener implements DisplaySelectListener
    {
        /* (non-Javadoc)
         * @see org.crosswire.bibledesktop.book.DisplaySelectListener#bookChosen(org.crosswire.bibledesktop.book.DisplaySelectEvent)
         */
        public void bookChosen(DisplaySelectEvent ev)
        {
            log.debug("new bible chosen: "+ev.getBook()); //$NON-NLS-1$

            Book book = ev.getBook();
            txtPassg.setBook(book);

            // The following way to refresh the view is a little harsh because
            // resets any list selections. It would be nice if we could get
            // away with calling selection(), however it doesn't seem to work.
            setPassage(ev.getPassage());
        }

        /* (non-Javadoc)
         * @see org.crosswire.bibledesktop.book.DisplaySelectListener#passageSelected(org.crosswire.bibledesktop.book.DisplaySelectEvent)
         */
        public void passageSelected(DisplaySelectEvent ev)
        {
            log.debug("new passage chosen: "+ev.getPassage().getName()); //$NON-NLS-1$
            setPassage(ev.getPassage());
        }
    }
}
