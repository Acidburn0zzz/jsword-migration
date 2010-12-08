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
package org.crosswire.common.config.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.crosswire.common.config.Choice;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.CWScrollPane;
import org.crosswire.common.swing.EdgeBorder;
import org.crosswire.common.swing.FormPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.Logger;

/**
 * A Configuration Editor that presents configuration as a Wizard.
 * 
 * <p>
 * A few of the ideas in this code came from an article in the JDJ about
 * configuration. However the Config package has a number of huge differences,
 * the biggest being what it does with its config info. The JDJ article assumed
 * that you'd only ever want to edit a properties file and that the rest of the
 * app didn't care much, and that the tree style view was the only one you would
 * ever need. This package is a re-write that addresses these shortcomings and
 * others.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class WizardConfigEditor extends AbstractConfigEditor {
    /**
     * Default constructor
     * 
     */
    public WizardConfigEditor() {
        actions = new ActionFactory(WizardConfigEditor.class, this);

        names = new ArrayList();
        layout = new CardLayout();
        deck = new JPanel(layout);

        // I18N(DMS)
        title = new JLabel(UserMsg.gettext("Preferences"), SwingConstants.LEADING);
        title.setIcon(TASK_ICON_LARGE);
        title.setFont(getFont().deriveFont(Font.PLAIN, 16));
        title.setPreferredSize(new Dimension(30, 30));
        title.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        title.setBackground(Color.gray);
        title.setForeground(Color.white);
        title.setOpaque(true);

        deck.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Use this if you want to have the tree touch the bottom. Then add
        // the button panel to content.South
        // JPanel content = new JPanel();
        // content.setLayout(new BorderLayout());
        // content.add(BorderLayout.CENTER, deck);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        panel.add(title, BorderLayout.PAGE_START);
        panel.add(deck, BorderLayout.CENTER);

        setLayout(new BorderLayout(5, 10));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(panel, BorderLayout.CENTER);
        add(getButtonPane(), BorderLayout.PAGE_END);
        GuiUtil.applyDefaultOrientation(this);
    }

    /**
     * <br />
     * Danger - this method is not called by the TreeConfigEditor constructor,
     * it is called by the AbstractConfigEditor constructor so any field
     * initializers will be called AFTER THIS METHOD EXECUTES so don't use field
     * initializers.
     */
    protected void initializeGUI() {
        // We need to Enumerate thru the Model names not the Path names in the
        // deck because the deck is a Hashtable that re-orders them.
        Iterator it = config.iterator();
        while (it.hasNext()) {
            Choice choice = (Choice) it.next();
            if (choice.isHidden()) {
                continue;
            }

            String key = choice.getKey();

            int last_dot = key.lastIndexOf('.');
            String path = key.substring(0, last_dot);

            FormPane card = (FormPane) decks.get(path);
            if (card.getParent() == null) {
                JScrollPane scroll = new CWScrollPane(card);
                scroll.setBorder(BorderFactory.createEmptyBorder());
                deck.add(path, scroll);
                wcards++;

                // The name for the title bar
                names.add(path.replace('.', ' '));
            }
        }

        title.setText(names.get(1) + LimboMsg.PROPERTIES_POSN.toString(new Object[] {
                Integer.valueOf(1), Integer.valueOf(wcards)
        }));

        SwingUtilities.updateComponentTreeUI(this);
    }

    /**
     * Now this wasn't created with JBuilder but maybe just maybe by calling my
     * method this, JBuilder may grok it.
     */
    protected void updateTree() {
    }

    /**
     * A Config panel does not have buttons. These are they.
     * 
     * @return A button panel
     */
    private JComponent getButtonPane() {

        finish = new JButton(actions.getAction(FINISH));
        next = new JButton(actions.getAction(NEXT));

        JPanel buttons = new JPanel();

        buttons.setLayout(new GridLayout(1, 2, 10, 10));
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttons.add(new JButton(actions.getAction(HELP)));
        buttons.add(new JButton(actions.getAction(CANCEL)));
        buttons.add(new JButton(actions.getAction(BACK)));
        buttons.add(next);
        buttons.add(finish);

        actions.getAction(HELP).setEnabled(false);
        actions.getAction(BACK).setEnabled(false);

        JPanel retcode = new JPanel(new BorderLayout(10, 10));

        retcode.setBorder(new EdgeBorder(SwingConstants.NORTH));
        retcode.add(buttons, BorderLayout.LINE_END);

        return retcode;
    }

    public void doWizardCancel() {
        hideDialog();
    }

    public void doWizardHelp() {
    }

    public void doWizardBack() {
        move(-1);
    }

    public void doWizardNext() {
        move(1);
    }

    public void doWizardFinish(ActionEvent ev) {
        screenToLocal();
        al.actionPerformed(ev);
        hideDialog();
    }

    /**
     * Set a new card to be visible
     */
    private void move(int dirn) {
        if (dirn == -1 && posn > 0) {
            layout.previous(deck);
            posn--;
        }

        if (dirn == 1 && posn < (wcards - 1)) {
            layout.next(deck);
            posn++;
        }

        title.setText(names.get(posn) + LimboMsg.PROPERTIES_POSN.toString(new Object[] {
                Integer.valueOf(posn + 1), Integer.valueOf(wcards)
        }));

        actions.getAction(BACK).setEnabled(posn != 0);
        actions.getAction(NEXT).setEnabled(posn != (wcards - 1));

        if (posn == wcards - 1) {
            dialog.getRootPane().setDefaultButton(finish);
        } else {
            dialog.getRootPane().setDefaultButton(next);
        }
    }

    /**
     * Create a dialog to house a TreeConfig component using the default set of
     * Fields. This version just sets the default button to next
     * 
     * @param parent
     *            A component to use to find a frame to use as a dialog parent
     */
    public void showDialog(Component parent) {
        Component root = SwingUtilities.getRoot(parent);
        dialog = new JDialog((JFrame) root);
        dialog.getRootPane().setDefaultButton(next);
        dialog.getContentPane().add(this);
        dialog.setTitle(config.getTitle());
        dialog.setSize(800, 500);
        dialog.pack();
        dialog.setModal(true);
        GuiUtil.applyDefaultOrientation(dialog);
        dialog.setVisible(true);

        // Why is this only available in Frames?
        // dialog.setIconImage(task_small);

        log.debug("Modal fails on SunOS, take care. os.name=" + System.getProperty("os.name"));
        if (!"SunOS".equals(System.getProperty("os.name")))
        {
            dialog.dispose();
            dialog = null;
        }
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        actions = new ActionFactory(WizardConfigEditor.class, this);
        is.defaultReadObject();
    }

    private static final String NEXT = "WizardNext";
    private static final String CANCEL = "WizardCancel";
    private static final String FINISH = "WizardFinish";
    private static final String HELP = "WizardHelp";
    private static final String BACK = "WizardBack";

    private transient ActionFactory actions;

    /**
     * The current position
     */
    private int posn;

    /**
     * The number of cards
     */
    private int wcards;

    /**
     * The list of path names
     */
    private List names;

    /**
     * The title for the config panels
     */
    private JLabel title;

    /**
     * Contains the configuration panels
     */
    private JPanel deck;

    /**
     * Layout for the config panels
     */
    private CardLayout layout;

    /**
     * The Ok button
     */
    private JButton finish;

    /**
     * The next button
     */
    private JButton next;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(WizardConfigEditor.class);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3258416148742484276L;
}
