package org.crosswire.bibledesktop.book;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;

/**
 * A custom list view that paints icons alongside the words.
 * This was a simple modification of DefaultListCellRenderer however something
 * has made us implement ListCellRenderer directory and I'm not sure what.
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
public class BookListCellRenderer extends JLabel implements ListCellRenderer
{
    /**
     * Constructs a default renderer object for an item in a list.
     */
    public BookListCellRenderer()
    {
        if (noFocus == null)
        {
            noFocus = BorderFactory.createEmptyBorder(1, 1, 1, 1);
        }

        setOpaque(true);
        setBorder(noFocus);
    }

    /**
     * This is the only method defined by ListCellRenderer.  We just
     * reconfigure the Jlabel each time we're called.
     * @param list The JLists that we are part of
     * @param value Value to display
     * @param index Cell index
     * @param selected Is the cell selected
     * @param focus Does the list and the cell have the focus
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focus)
    {
        if (selected)
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        else
        {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        if (value == null)
        {
            setText(Msg.NONE.toString());
            setToolTipText(null);
            setIcon(null);
            setEnabled(false);
        }

        // Hack to allow us to use PROTOTYPE_BOOK_NAME as a prototype value
        if (value instanceof String)
        {
            String str = (String) value;

            setText(str);
            setToolTipText(null);
            setIcon(null);

            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setBorder(focus ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocus); //$NON-NLS-1$
        }

        if (value instanceof BookMetaData)
        {
            BookMetaData bmd = (BookMetaData) value;

            String displayName = bmd.toString();
            setText(displayName);
            setToolTipText(displayName);

            BookType type = bmd.getType();
            if (type.equals(BookType.BIBLE))
            {
                setIcon(ICON_BIBLE);
            }
            else if (type.equals(BookType.COMMENTARY))
            {
                setIcon(ICON_COMNT);
            }
            else if (type.equals(BookType.DICTIONARY))
            {
                setIcon(ICON_DICT);
            }

            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setBorder(focus ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocus); //$NON-NLS-1$
        }

        return this;
    }

    /**
     * The small version icon
     */
    private static final ImageIcon ICON_BIBLE = GuiUtil.getIcon("images/book-b16.png"); //$NON-NLS-1$

    /**
     * The small version icon
     */
    private static final ImageIcon ICON_COMNT = GuiUtil.getIcon("images/book-c16.png"); //$NON-NLS-1$

    /**
     * The small version icon
     */
    private static final ImageIcon ICON_DICT = GuiUtil.getIcon("images/book-d16.png"); //$NON-NLS-1$

    /**
     * border if we do not have focus
     */
    private static Border noFocus;

    /**
     * Make sure that book names are not too wide
     */
    public static final String PROTOTYPE_BOOK_NAME = "012345678901234567890123456789"; //$NON-NLS-1$

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3978138859576308017L;
}
