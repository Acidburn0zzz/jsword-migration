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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;

import org.crosswire.common.config.Choice;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.CWMsg;
import org.crosswire.common.swing.CWOptionPane;
import org.crosswire.common.swing.CWScrollPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.swing.CWOtherMsg;
import org.crosswire.common.util.Convert;

/**
 * A StringArrayField allows editing of an array of Strings in a JList. It
 * allows the user to specify additional classes that extend the functionality
 * of the program.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class StringArrayField extends JPanel implements Field {
    /**
     * Create a PropertyHashtableField for editing String arrays.
     */
    public StringArrayField() {
        actions = new ActionFactory(this);

        list_model = new DefaultComboBoxModel();
        list = new JList(list_model);

        JPanel buttons = new JPanel(new FlowLayout());

        list.setFont(new Font("Monospaced", Font.PLAIN, 12));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new CWScrollPane(list);

        // TRANSLATOR: This is the text on an "Add" button.
        buttons.add(new JButton(actions.addAction("Add", CWMsg.gettext("Add"))));
        // TRANSLATOR: This is the text on a "Remove" button.
        buttons.add(new JButton(actions.addAction("Remove", CWMsg.gettext("Remove"))));
        // TRANSLATOR: This is the text on an "Update" button.
        buttons.add(new JButton(actions.addAction("Update", CWMsg.gettext("Update"))));

        Border title = BorderFactory.createTitledBorder(CWOtherMsg.lookupText("Component Editor"));
        Border pad = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        setBorder(BorderFactory.createCompoundBorder(title, pad));

        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
        add(buttons, BorderLayout.PAGE_END);
        GuiUtil.applyDefaultOrientation(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.config.swing.Field#setChoice(org.crosswire.common
     * .config.Choice)
     */
    public void setChoice(Choice param) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.swing.Field#getValue()
     */
    public String getValue() {
        return Convert.stringArray2String(getArray(), SEPARATOR);
    }

    /**
     * Return the actual Hashtable being edited
     * 
     * @return The current value
     */
    public String[] getArray() {
        String[] retcode = new String[list_model.getSize()];
        for (int i = 0; i < retcode.length; i++) {
            retcode[i] = (String) list_model.getElementAt(i);
        }

        return retcode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.swing.Field#setValue(java.lang.String)
     */
    public void setValue(String value) {
        setArray(Convert.string2StringArray(value, SEPARATOR));
    }

    /**
     * Set the current value using a hashtable
     * 
     * @param value
     *            The new text
     */
    public void setArray(String[] value) {
        list_model = new DefaultComboBoxModel(value.clone());
        list.setModel(list_model);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.swing.Field#getComponent()
     */
    public JComponent getComponent() {
        return this;
    }

    /**
     * Pop up a dialog to allow editing of a new value
     */
    public void doAddEntry() {
        InputPane input = new InputPane();

        if (CWOptionPane.showConfirmDialog(this, input, CWOtherMsg.lookupText("New Class"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String new_name = input.name_field.getText();

            list_model.addElement(new_name);
        }
    }

    /**
     * Pop up a dialog to allow editing of a current value
     */
    public void doUpdateEntry() {
        InputPane input = new InputPane();
        input.name_field.setText(currentValue());

        if (CWOptionPane.showConfirmDialog(this, input, CWOtherMsg.lookupText("Edit Class"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String new_name = input.name_field.getText();

            list_model.removeElement(currentValue());
            list_model.addElement(new_name);
        }
    }

    /**
     * Delete the current value in the hashtable
     */
    public void doRemoveEntry() {
        list_model.removeElement(currentValue());
    }

    /**
     * What is the currently selected value?
     * 
     * @return The currently selected value
     */
    private String currentValue() {
        return (String) list_model.getElementAt(list.getSelectedIndex());
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        actions = new ActionFactory(this);
        is.defaultReadObject();
    }

    /**
     * The panel for a JOptionPane that allows editing a name/class combination.
     */
    public static class InputPane extends JPanel {
        /**
         * Simple ctor
         */
        public InputPane() {
            super(new GridBagLayout());

            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.LINE_END;
            c.insets = new Insets(0, 5, 0, 5);

            c.gridwidth = GridBagConstraints.RELATIVE; // next-to-last
            c.fill = GridBagConstraints.NONE; // reset to default
            c.weightx = 0.0; // reset to default
            add(new JLabel(CWOtherMsg.lookupText("Name") + ':'), c);

            c.gridwidth = GridBagConstraints.REMAINDER; // end row
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            add(name_field, c);

            setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        }

        /**
         * To edit a name (hashtable key)
         */
        protected JTextField name_field = new JTextField();

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3256444715753878326L;
    }

    /**
     * What character do we use to separate strings?
     */
    private static final String SEPARATOR = "#";

    private transient ActionFactory actions;

    /**
     * The TableModel that points the JTable at the Hashtable
     */
    private DefaultComboBoxModel list_model;

    /**
     * The Table - displays the Hashtble
     */
    private JList list;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256444715753878326L;
}
