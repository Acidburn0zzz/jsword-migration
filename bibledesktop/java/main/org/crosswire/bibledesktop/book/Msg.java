package org.crosswire.bibledesktop.book;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
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
class Msg extends MsgBase
{
    static final Msg EMPTY_FILE = new Msg("File {0} is empty"); //$NON-NLS-1$
    static final Msg VERSE_LIST_DESC = new Msg("Verse Lists ({0})"); //$NON-NLS-1$
    static final Msg ERROR_READING = new Msg("Error reading value: {0}"); //$NON-NLS-1$
    static final Msg BOOKS = new Msg("Books"); //$NON-NLS-1$
    static final Msg SELECT_BOOK = new Msg("Select a book"); //$NON-NLS-1$
    static final Msg SELECT_CHAPTER = new Msg("Select a chapter"); //$NON-NLS-1$
    static final Msg SELECT_VERSE = new Msg("Select a verse"); //$NON-NLS-1$
    static final Msg NONE = new Msg("None"); //$NON-NLS-1$

    static final Msg CHOOSER_CANCEL = new Msg("Cancel"); //$NON-NLS-1$
    static final Msg CHOOSER_OK = new Msg("OK"); //$NON-NLS-1$
    static final Msg CHOOSER_TITLE = new Msg("Select a Bible"); //$NON-NLS-1$

    static final Msg BAD_VERSE = new Msg("Error finding verse"); //$NON-NLS-1$
    static final Msg NO_INSTALLED_BIBLE = new Msg("No Bible is installed"); //$NON-NLS-1$
    static final Msg SELECT_PASSAGE_TITLE = new Msg("Select Passage"); //$NON-NLS-1$
    static final Msg UNTITLED = new Msg("Untitled {0}"); //$NON-NLS-1$

    static final Msg AVAILABLE_BOOKS = new Msg("Available Books"); //$NON-NLS-1$
    static final Msg LOCAL_BOOKS = new Msg("Local"); //$NON-NLS-1$

    static final Msg EDIT_SITE_TITLE = new Msg("Edit Update Sites"); //$NON-NLS-1$
    static final Msg MISSING_SITE = new Msg("Missing site name"); //$NON-NLS-1$
    static final Msg DUPLICATE_SITE = new Msg("Duplicate site name"); //$NON-NLS-1$
    static final Msg NO_SELECTED_SITE = new Msg("No selected site to edit"); //$NON-NLS-1$
    static final Msg NO_SITE = new Msg("No Site"); //$NON-NLS-1$
    static final Msg CONFIRM_DELETE_SITE = new Msg("Are you sure you want to delete {0}"); //$NON-NLS-1$
    static final Msg DELETE_SITE = new Msg("Delete Site?"); //$NON-NLS-1$

    static final Msg ERROR = new Msg("Error: {0}"); //$NON-NLS-1$
    static final Msg SUMMARY = new Msg("Summary: {0}"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
