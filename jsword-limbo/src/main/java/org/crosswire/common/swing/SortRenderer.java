/**
 * Distribution License:
 * This is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published
 * by the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/llgpl.html
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
package org.crosswire.common.swing;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 * A SortRenderer indicates the column that is sorted by italizing it.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @author Joe Walker [joe at eireneh dot com]
 */
public class SortRenderer extends DefaultTableCellRenderer {
    /**
     * Constructor for SortRenderer
     * 
     * @param stm
     *            SegmentTableModel
     */
    public SortRenderer(RowTableModel stm) {
        model = stm;
        pressedColumn = null;
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Method getTableCellRendererComponent
     * 
     * @param table
     *            JTable
     * @param value
     *            Object
     * @param isSelected
     *            boolean
     * @param hasFocus
     *            boolean
     * @param row
     *            int
     * @param column
     *            int
     * @return Component
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (table != null) {
            setToolTipText(model.getHeaderToolTip(column));
            final JTableHeader header = table.getTableHeader();
            final TableColumn tableColumn = table.getColumnModel().getColumn(column);
            if (header != null) {
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                final Font headerFont = header.getFont();
                if (tableColumn == pressedColumn) {
                    setFont(headerFont.deriveFont(Font.ITALIC));
                } else {
                    setFont(headerFont);
                }
            }
        }

        setText((value == null) ? "" : value.toString());
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        return this;
    }

    /**
     * Method getPressedColumn
     * 
     * @return the table column
     */
    public TableColumn getPressedColumn() {
        return pressedColumn;
    }

    /**
     * Method setPressedColumn
     * 
     * @param tc
     *            the table column
     */
    public void setPressedColumn(TableColumn tc) {
        pressedColumn = tc;
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3977303200573765939L;

    /**
     * Field pressedColumn
     */
    private TableColumn pressedColumn;

    /**
     * Field model
     */
    private RowTableModel model;
}
