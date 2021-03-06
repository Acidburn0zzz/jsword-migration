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

import java.awt.Component;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.Books;

/**
 * A ListModel that shows the regestered BookDrivers.
 * 
 * <p>
 * DriversListModel can be set to read-only mode where it will display only the
 * BookDrivers that can receive new Book data.
 * </p>
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class DriversListModel extends AbstractListModel {
    /**
     * Basic constructor
     */
    public DriversListModel(boolean includeRo) {
        if (includeRo) {
            drivers = Books.installed().getDrivers();
        } else {
            drivers = Books.installed().getWritableDrivers();
        }
    }

    /**
     * Basic constructor
     */
    public DriversListModel() {
        this(true);
    }

    /**
     * Returns the length of the list.
     */
    public int getSize() {
        return drivers.length;
    }

    /**
     * Returns the value at the specified index.
     */
    public Object getElementAt(int index) {
        if (index >= drivers.length) {
            return null;
        }

        return drivers[index].getClass().getName();
    }

    /**
     * Given an item, work out the name of the Bible that it represents
     * 
     * @param test
     *            The item from the list
     * @return A Bible name
     */
    public String getDriverName(Object test) {
        String item = test.toString();
        int end = item.indexOf(" (");
        return item.substring(0, end);
    }

    /**
     * Given an item, work out the name of the Driver that it represents
     * 
     * @param test
     *            The item from the list
     * @return A Driver
     */
    public BookDriver getDriver(Object test) {
        return drivers[getIndexOf(test)];
    }

    /**
     * Returns the index-position of the specified object in the list.
     * 
     * @param test
     *            the object to find
     * @return an int representing the index position, where 0 is the first
     *         position
     */
    public int getIndexOf(Object test) {
        for (int i = 0; i < drivers.length; i++) {
            if (test.equals(getElementAt(i))) {
                return i;
            }
        }

        return -1;
    }

    /**
     * The array of drivers
     */
    protected BookDriver[] drivers;

    /**
     * The small version icon
     */
    protected static final Icon SMALL_ICON = GuiUtil.getIcon("/org/crosswire/resources/task_small.gif");

    /**
     * border if we do not have focus
     */
    protected static final Border NO_FOCUS_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3689068456540910136L;

    /**
     * Create a BookListCellRenderer
     */
    public static ListCellRenderer getListCellRenderer() {
        return new BibleListCellRenderer();
    }

    /**
     * A custom list view that paints icons alongside the words. This is a
     * simple modification of DeafultListCellRenderer
     */
    public static class BibleListCellRenderer extends JLabel implements ListCellRenderer {
        /**
         * Constructs a default renderer object for an item in a list.
         */
        public BibleListCellRenderer() {
            setOpaque(true);
            setBorder(NO_FOCUS_BORDER);
        }

        /**
         * This is the only method defined by ListCellRenderer. We just
         * reconfigure the Jlabel each time we're called.
         * 
         * @param list
         *            The JLists that we are part of
         * @param value
         *            Value to display
         * @param index
         *            Cell index
         * @param selected
         *            Is the cell selected
         * @param focus
         *            Does the list and the cell have the focus
         */
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focus) {
            if (selected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setText((value == null) ? "" : value.toString());
            setIcon(SMALL_ICON);

            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setBorder(focus ? UIManager.getBorder("List.focusCellHighlightBorder") : NO_FOCUS_BORDER);

            return this;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3256722892245971512L;
    }
}
