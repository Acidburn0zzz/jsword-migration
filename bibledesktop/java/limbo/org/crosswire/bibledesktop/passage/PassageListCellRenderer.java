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

import java.awt.Component;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.passage.VerseRange;

/**
 * Renders a Passage in a JList.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class PassageListCellRenderer implements ListCellRenderer, Serializable
{
    /**
     * Constructs a default renderer object for an item in a list.
     */
    public PassageListCellRenderer(Book bible)
    {
        this.bible = bible;

        border = new EmptyBorder(1, 1, 1, 1);

        label.setBorder(border);
        label.setOpaque(true);
        label.setIcon(GuiUtil.getIcon("images/Passage16.gif")); //$NON-NLS-1$
    }

    /**
     * Customize something to display the Passage component
     * @return The customized component
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focus)
    {
        if (selected)
        {
            label.setBackground(list.getSelectionBackground());
            label.setForeground(list.getSelectionForeground());
        }
        else
        {
            label.setBackground(list.getBackground());
            label.setForeground(list.getForeground());
        }

        if (value instanceof VerseRange)
        {
            try
            {
                VerseRange range = (VerseRange) value;
                String text = (String) hash.get(range);

                if (text == null)
                {
                    BookData bdata = bible.getData(range);
                    String simple = bdata.getVerseText();
                    text = "<html><b>" + range.getName() + "</b> " + simple; //$NON-NLS-1$ //$NON-NLS-2$
                    hash.put(range, text);
                }

                label.setText(text);
            }
            catch (Exception ex)
            {
                Reporter.informUser(this, ex);
                label.setText(Msg.ERROR.toString());
            }
        }
        else
        {
            label.setText((value == null) ? "" : value.toString()); //$NON-NLS-1$
        }

        label.setEnabled(list.isEnabled());
        label.setFont(list.getFont());
        label.setBorder(focus ? UIManager.getBorder("List.focusCellHighlightBorder") : border); //$NON-NLS-1$

        return label;
    }

    /**
     * The Bible in which to look up verses
     */
    private Book bible;

    /**
     * The label to display if the item is not selected
     */
    private JLabel label = new JLabel();

    /**
     * The border if the label is selected
     */
    private Border border;

    /**
     * A cache of Bible texts
     */
    private Map hash = new HashMap();

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3978423624430270256L;
}
