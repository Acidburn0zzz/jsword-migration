package org.crosswire.bibledesktop.book;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.crosswire.bibledesktop.passage.KeyChangeEvent;
import org.crosswire.bibledesktop.passage.KeyChangeListener;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.RestrictionType;

/**
 * Passage Selection area.
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
public class DisplaySelectPane extends JPanel implements KeyChangeListener
{
    /**
     * General constructor
     */
    public DisplaySelectPane()
    {
        init();
    }

    /**
     * Initialize the GUI
     */
    private void init()
    {
        title = Msg.UNTITLED.toString(new Integer(base++));

        actions = new ActionFactory(DisplaySelectPane.class, this);

        // Create a way for selecting how passages are found
        JPanel pnlSelect = new JPanel(new GridBagLayout());

        // Layout the card picker and the Bible picker side by side
        pnlSelect.add(createRadioPanel(), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                        new Insets(0, 2, 0, 2), 0, 0));
        pnlSelect.add(createBiblePicker(), new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,
                        2, 0, 2), 0, 0));

        // Create a deck of "cards" for the different ways of finding passages
        layCards = new CardLayout();
        pnlCards = new JPanel(layCards);

        pnlCards.add(createPassagePanel(), PASSAGE);
        pnlCards.add(createSearchPanel(), SEARCH);
        pnlCards.add(createMatchPanel(), MATCH);

        this.setLayout(new BorderLayout());
        this.add(pnlSelect, BorderLayout.PAGE_START);
        this.add(pnlCards, BorderLayout.CENTER);

    }

    /**
     *
     */
    private Component createRadioPanel()
    {
        // These buttons control which "card" in the deck is shown
        rdoPassg = new JRadioButton(actions.getAction(PASSAGE_OPTIONS));
        rdoMatch = new JRadioButton(actions.getAction(MATCH_OPTIONS));
        rdoSearch = new JRadioButton(actions.getAction(SEARCH_OPTIONS));

        ButtonGroup grpType = new ButtonGroup();
        grpType.add(rdoPassg);
        grpType.add(rdoSearch);
        grpType.add(rdoMatch);

        JPanel pnlRadios = new JPanel();
        pnlRadios.add(rdoPassg, null);
        pnlRadios.add(rdoSearch, null);
        pnlRadios.add(rdoMatch, null);
        rdoPassg.setSelected(true);

        return pnlRadios;
    }

    /**
     *
     */
    private Component createBiblePicker()
    {
        // Create the Bible picker
        // search() and version() rely on this returning only Bibles
        mdlVersn = new BooksComboBoxModel(BookFilters.getBibles());
        JComboBox cboVersn = new JComboBox(mdlVersn);
        Object selected = mdlVersn.getSelectedItem();
        if (selected != null)
        {
            cboVersn.setToolTipText(selected.toString());
        }
        cboVersn.setRenderer(new BookListCellRenderer());
        // Allow for layout to size the combo box
        cboVersn.setPrototypeDisplayValue(" "); //$NON-NLS-1$
        cboVersn.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ev)
            {
                if (ev.getStateChange() == ItemEvent.SELECTED)
                {
                    changeVersion();
                    JComboBox combo = (JComboBox) ev.getSource();
                    combo.setToolTipText(combo.getSelectedItem().toString());
                }
            }
        });
        return cboVersn;
    }

    /**
     *
     */
    private Component createPassagePanel()
    {
        JLabel label = actions.createJLabel(VIEW_LABEL);

        txtPassg = new JTextField();
        txtPassg.setAction(actions.getAction(PASSAGE_FIELD));
        txtPassg.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent ev)
            {
                if (ev.getKeyChar() == '\n' && ev.getModifiers() == Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())
                {
                    showSelectDialog();
                }
            }
        });

        JButton btnDialg = new JButton(actions.getAction(MORE));

        JButton goButton = new JButton(actions.getAction(GO_PASSAGE));

        JPanel panel = new JPanel(new GridBagLayout());

        panel.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));
        panel.add(txtPassg, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 2, 2), 0, 0));
        panel.add(goButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));
        panel.add(btnDialg, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        return panel;
    }

    /**
     *
     */
    private Component createSearchPanel()
    {
        txtSearch = new JTextField();
        txtSearch.setAction(actions.getAction(SEARCH_FIELD));

        JLabel label = actions.createJLabel(SEARCH_LABEL);
        label.setLabelFor(txtSearch);

        chkSRestrict = new JCheckBox(actions.getAction(RESTRICT_SEARCH));
        chkSRestrict.setSelected(false);

        txtSRestrict = new JTextField();
        Action action = actions.getAction(SEARCH_RESTRICTION);
        txtSRestrict.setAction(action);
        txtSRestrict.setText(action.getValue(Action.NAME).toString());

        JButton goButton = new JButton(actions.getAction(GO_SEARCH));

        JPanel panel = new JPanel(new GridBagLayout());

        panel.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));
        panel.add(txtSearch, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 2, 2), 0, 0));
        panel.add(goButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));
        panel.add(chkSRestrict, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        panel.add(txtSRestrict, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2),
                        0, 0));

        return panel;
    }

    /**
     *
     */
    private Component createMatchPanel()
    {
        txtMatch = new JTextField();
        txtMatch.setAction(actions.getAction(MATCH_FIELD));

        JLabel label = actions.createJLabel(MATCH_LABEL);
        label.setLabelFor(txtMatch);

        txtMRestrict = new JTextField();
        Action action = actions.getAction(MATCH_RESTRICTION);
        txtMRestrict.setAction(action);
        txtMRestrict.setText(action.getValue(Action.NAME).toString());

        chkMRestrict = new JCheckBox(actions.getAction(RESTRICT_MATCH));
        chkMRestrict.setSelected(false);

        JButton goButton = new JButton(actions.getAction(GO_MATCH));

        JPanel panel = new JPanel(new GridBagLayout());

        panel.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));
        panel.add(txtMatch, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 2, 2, 2), 0, 0));
        panel.add(goButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));
        panel.add(chkMRestrict, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        panel.add(txtMRestrict, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

        return panel;
    }

    /**
     * What is the currently selected Book?
     */
    public Book getBook()
    {
        BookMetaData bmd = mdlVersn.getSelectedBookMetaData();
        return bmd != null ? bmd.getBook() : null;
    }

    public void clear()
    {
        if (isClear())
        {
            return;
        }

        txtPassg.setText(""); //$NON-NLS-1$
        flipOption(PASSAGE);

        Action action = actions.getAction(SEARCH_RESTRICTION);
        txtSearch.setText(""); //$NON-NLS-1$
        txtSRestrict.setText(action.getValue(Action.NAME).toString());
        chkSRestrict.setSelected(false);

        action = actions.getAction(MATCH_RESTRICTION);
        txtMatch.setText(""); //$NON-NLS-1$
        txtMRestrict.setText(action.getValue(Action.NAME).toString());
        chkMRestrict.setSelected(false);

        title = Msg.UNTITLED.toString(new Integer(base++));

        updateDisplay();

    }

    public boolean isClear()
    {
        return title.indexOf(Msg.CLEAR.toString()) != -1;
    }

    // The following are behaviors associated with the actions executed by actionPerformed
    public void doPassageOption()
    {
        flipOption(PASSAGE);
    }

    public void doMatchOption()
    {
        flipOption(MATCH);
    }

    public void doSearchOption()
    {
        flipOption(SEARCH);
    }

    private void flipOption(String toWhat)
    {
        layCards.show(pnlCards, toWhat);
        adjustFocus();
    }

    // More (...) button was clicked
    public void doMore()
    {
        showSelectDialog();
    }

    // Go button was clicked
    public void doGoPassage()
    {
        doPassageAction();
    }

    // Go button was clicked
    public void doGoSearch()
    {
        doSearchAction();
    }

    // Enter was hit in txtRestrict
    public void doSearchEverywhere()
    {
        doSearchAction();
    }

    // Go button was clicked
    public void doGoMatch()
    {
        doMatchAction();
    }

    // Enter was hit in txtMRestrict
    public void doMatchAnywhere()
    {
        doMatchAction();
    }

    /**
     * Someone pressed return in the passage area
     */
    public void doPassageAction()
    {
        setTitle(txtPassg.getText());
        updateDisplay();
    }

    /**
     * Someone pressed return in the search area
     */
    public void doSearchAction()
    {
        BookMetaData bmd = mdlVersn.getSelectedBookMetaData();
        if (bmd == null)
        {
            noBookInstalled();
            return;
        }

        try
        {
            String param = txtSearch.getText();
            Book book = bmd.getBook();

            Search search = new Search(param, false);
            if (chkSRestrict.isSelected())
            {
                Key restrict = book.getKey(txtSRestrict.getText());
                search.setRestriction(restrict);
            }

            Key key = book.find(search);

            txtPassg.setText(key.getName());
            setTitle(param);
            updateDisplay();
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Someone pressed return in the match area
     */
    public void doMatchAction()
    {
        BookMetaData bmd = mdlVersn.getSelectedBookMetaData();
        if (bmd == null)
        {
            noBookInstalled();
            return;
        }

        try
        {
            String param = txtMatch.getText();
            Book book = bmd.getBook();

            Search search = new Search(param, true);
            if (chkMRestrict.isSelected())
            {
                Key restrict = book.getKey(txtMRestrict.getText());
                search.setRestriction(restrict);
            }

            Key key = book.find(search);

            // we get PassageTallys for best match searches
            if (key instanceof PassageTally)
            {
                PassageTally tally = (PassageTally) key;
                tally.setOrdering(PassageTally.ORDER_TALLY);
                tally.trimRanges(20, RestrictionType.NONE);
            }

            txtPassg.setText(key.getName());

            setTitle(param);
            updateDisplay();
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Some on (un)checked the restrict match box
     */
    public void doRestrictMatch()
    {
        boolean selected = chkMRestrict.isSelected();
        txtMRestrict.setEnabled(selected);
    }

    /**
     * Some on (un)checked the restrict search box
     */
    public void doRestrictSearch()
    {
        boolean selected = chkSRestrict.isSelected();
        txtSRestrict.setEnabled(selected);

    }

    /**
     * Sync the viewed passage with the passage text box
     */
    private void updateDisplay()
    {
        BookMetaData bmd = mdlVersn.getSelectedBookMetaData();
        if (bmd == null)
        {
            noBookInstalled();
            return;
        }

        try
        {
            Book book = bmd.getBook();
            Key key = book.getKey(txtPassg.getText());

            fireCommandMade(new DisplaySelectEvent(this, key, book));
        }
        catch (NoSuchVerseException ex)
        {
            JOptionPane.showMessageDialog(this, ex.getMessage(), Msg.BAD_VERSE.toString(), JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Set the focus to the right initial component
     */
    public void adjustFocus()
    {
        if (rdoPassg.isSelected())
        {
            txtPassg.grabFocus();
        }
        else if (rdoSearch.isSelected())
        {
            txtSearch.grabFocus();
        }
        else if (rdoMatch.isSelected())
        {
            txtMatch.grabFocus();
        }
    }

    /**
     * Accessor for the default name
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the default name
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Sets the default name
     */
    public void setText(String text)
    {
        String currentText = txtPassg.getText();
        if (!currentText.equals(text))
        {
            txtPassg.setText(text);
            setTitle(text);
            updateDisplay();
        }
    }

    /**
     * Someone changed the version combo
     */
    protected void changeVersion()
    {
        BookMetaData bmd = mdlVersn.getSelectedBookMetaData();
        if (bmd == null)
        {
            noBookInstalled();
            return;
        }

        try
        {
            Book book = bmd.getBook();
            Key key = book.getKey(txtPassg.getText());

            fireVersionChanged(new DisplaySelectEvent(this, key, book));
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Display a dialog indicating that no Bible is installed.
     */
    private void noBookInstalled()
    {
        String noBible = Msg.NO_INSTALLED_BIBLE.toString();
        JOptionPane.showMessageDialog(this, noBible, noBible, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Someone clicked the "..." button
     */
    protected void showSelectDialog()
    {
        if (dlgSelect == null)
        {
            dlgSelect = new PassageSelectionPane();
        }
        String passg = dlgSelect.showInDialog(this, Msg.SELECT_PASSAGE_TITLE.toString(), true, txtPassg.getText());
        txtPassg.setText(passg);
        doPassageAction();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.KeyChangeListener#keyChanged(org.crosswire.bibledesktop.book.KeyChangeEvent)
     */
    public void keyChanged(KeyChangeEvent ev)
    {
        String text = ev.getKey().getName();
        setText(text);
    }

    /**
     * Add a command listener
     */
    public synchronized void addCommandListener(DisplaySelectListener li)
    {
        List temp = new ArrayList(2);

        if (listeners != null)
        {
            temp.addAll(listeners);
        }

        if (!temp.contains(li))
        {
            temp.add(li);
            listeners = temp;
        }
    }

    /**
     * Remove a command listener
     */
    public synchronized void removeCommandListener(DisplaySelectListener li)
    {
        if (listeners != null && listeners.contains(li))
        {
            List temp = new ArrayList();
            temp.addAll(listeners);

            temp.remove(li);
            listeners = temp;
        }
    }

    /**
     * Inform the command listeners
     */
    protected void fireCommandMade(DisplaySelectEvent ev)
    {
        if (listeners != null)
        {
            for (int i = 0; i < listeners.size(); i++)
            {
                DisplaySelectListener li = (DisplaySelectListener) listeners.get(i);
                li.passageSelected(ev);
            }
        }
    }

    /**
     * Inform the version listeners
     */
    protected void fireVersionChanged(DisplaySelectEvent ev)
    {
        if (listeners != null)
        {
            int count = listeners.size();
            for (int i = 0; i < count; i++)
            {
                ((DisplaySelectListener) listeners.get(i)).bookChosen(ev);
            }
        }
    }

    // Enumeration of all the keys to known actions
    private static final String PASSAGE_OPTIONS = "PassageOption"; //$NON-NLS-1$
    private static final String MATCH_OPTIONS = "MatchOption"; //$NON-NLS-1$
    private static final String SEARCH_OPTIONS = "SearchOption"; //$NON-NLS-1$

    // For the Passage card
    private static final String VIEW_LABEL = "ViewLabel"; //$NON-NLS-1$
    private static final String PASSAGE_FIELD = "PassageAction"; //$NON-NLS-1$
    private static final String MORE = "More"; //$NON-NLS-1$
    private static final String GO_PASSAGE = "GoPassage"; //$NON-NLS-1$

    // for the Search card
    private static final String SEARCH_LABEL = "SearchLabel"; //$NON-NLS-1$
    private static final String RESTRICT_SEARCH = "RestrictSearch"; //$NON-NLS-1$
    private static final String GO_SEARCH = "GoSearch"; //$NON-NLS-1$
    private static final String SEARCH_FIELD = "SearchAction"; //$NON-NLS-1$
    private static final String SEARCH_RESTRICTION = "SearchEverywhere"; //$NON-NLS-1$

    // for the Match card
    private static final String MATCH_LABEL = "MatchLabel"; //$NON-NLS-1$
    private static final String RESTRICT_MATCH = "RestrictMatch"; //$NON-NLS-1$
    private static final String GO_MATCH = "GoMatch"; //$NON-NLS-1$
    private static final String MATCH_FIELD = "MatchAction"; //$NON-NLS-1$
    private static final String MATCH_RESTRICTION = "MatchAnywhere"; //$NON-NLS-1$

    private static final String PASSAGE = "p"; //$NON-NLS-1$
    private static final String SEARCH = "s"; //$NON-NLS-1$
    private static final String MATCH = "m"; //$NON-NLS-1$

    private static int base = 1;

    private String title;

    private transient List listeners;

    private BooksComboBoxModel mdlVersn;
    private PassageSelectionPane dlgSelect;
    private JTextField txtPassg;
    private JTextField txtSearch;
    protected JCheckBox chkSRestrict;
    protected JTextField txtSRestrict;
    private JTextField txtMatch;
    protected JCheckBox chkMRestrict;
    protected JTextField txtMRestrict;
    private JRadioButton rdoMatch;
    private JRadioButton rdoSearch;
    private JRadioButton rdoPassg;
    private JPanel pnlCards;
    private CardLayout layCards;

    private ActionFactory actions;

    /**
     * SERIALUID(dms): A placeholder for the ultimate version id.
     */
    private static final long serialVersionUID = 1L;
}

