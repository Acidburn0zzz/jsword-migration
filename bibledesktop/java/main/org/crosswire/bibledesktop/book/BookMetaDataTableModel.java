package org.crosswire.bibledesktop.book;

import org.crosswire.common.swing.MapTableModel;
import org.crosswire.jsword.book.BookMetaData;

/**
 * A TableModel that displays the data in a BookMetaData object.
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
public class BookMetaDataTableModel extends MapTableModel
{
	/**
     * Simple ctor
     */
    public BookMetaDataTableModel()
    {
        setBookMetaData(null);
    }

    /**
     * Simple ctor with default BookMetaData
     */
    public BookMetaDataTableModel(BookMetaData bmd)
    {
        setBookMetaData(bmd);
    }

    /**
     * @return Returns the BookMetaData.
     */
    public BookMetaData getBookMetaData()
    {
        return bmd;
    }

    /**
     * @param bmd The BookMetaData to set.
     */
    public final void setBookMetaData(BookMetaData bmd)
    {
        if (bmd != this.bmd)
        {
            if (bmd == null)
            {
                setMap(null);
            }
            else
            {
                setMap(bmd.getProperties());
            }

            this.bmd = bmd;
        }
    }

    /**
     * The meta data that we are displaying
     */
    private BookMetaData bmd;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257566222043460664L;
}
