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
    static final Msg EMPTY_FILE = new Msg("BibleViewPane.EmptyFile"); //$NON-NLS-1$
    static final Msg VERSE_LIST_DESC = new Msg("BibleViewPane.VerseListDesc"); //$NON-NLS-1$
    static final Msg BOOKS = new Msg("BibleViewPane.Books"); //$NON-NLS-1$
    static final Msg SELECT_BOOK = new Msg("BibleViewPane.SelectBook"); //$NON-NLS-1$
    static final Msg SELECT_CHAPTER = new Msg("BibleViewPane.SelectChapter"); //$NON-NLS-1$
    static final Msg SELECT_VERSE = new Msg("BibleViewPane.SelectVerse"); //$NON-NLS-1$
    static final Msg NONE = new Msg("BibleViewPane.None"); //$NON-NLS-1$

    // I18N: migrate this to an ActionFactory
    static final Msg CHOOSER_CANCEL = new Msg("BookChooser.Cancel"); //$NON-NLS-1$
    // I18N: migrate this to an ActionFactory
    static final Msg CHOOSER_OK = new Msg("BookChooser.OK"); //$NON-NLS-1$
    static final Msg CHOOSER_TITLE = new Msg("BookChooser.Title"); //$NON-NLS-1$

    static final Msg BAD_VERSE = new Msg("DisplaySelectPane.BadVerse"); //$NON-NLS-1$
    static final Msg NO_INSTALLED_BIBLE = new Msg("DisplaySelectPane.NoInstalledBible"); //$NON-NLS-1$
    static final Msg SELECT_PASSAGE_TITLE = new Msg("DisplaySelectPane.SelectPassageTitle"); //$NON-NLS-1$
    static final Msg CLEAR = new Msg("DisplaySelectPane.Clear"); //$NON-NLS-1$
    static final Msg UNTITLED = new Msg("DisplaySelectPane.Untitled"); //$NON-NLS-1$

    static final Msg AVAILABLE_BOOKS = new Msg("SitesPane.AvailableBooks"); //$NON-NLS-1$
    static final Msg LOCAL_BOOKS = new Msg("SitesPane.Local"); //$NON-NLS-1$

    static final Msg SIZE = new Msg("SitePane.Size"); //$NON-NLS-1$
    static final Msg CONFIRMATION_TITLE = new Msg("SitePane.ConfirmationTitle"); //$NON-NLS-1$
    static final Msg INSTALLED = new Msg("SitePane.Installed"); //$NON-NLS-1$

    static final Msg EDIT_SITE_TITLE = new Msg("EditSitePane.EditSitesTitle"); //$NON-NLS-1$
    static final Msg MISSING_SITE = new Msg("EditSitePane.MissingSite"); //$NON-NLS-1$
    static final Msg DUPLICATE_SITE = new Msg("EditSitePane.DuplicateSite"); //$NON-NLS-1$
    static final Msg NO_SELECTED_SITE = new Msg("EditSitePane.NoSelectedSite"); //$NON-NLS-1$
    static final Msg NO_SITE = new Msg("EditSitePane.NoSite"); //$NON-NLS-1$
    static final Msg CONFIRM_DELETE_SITE = new Msg("EditSitePane.ConfirmDeleteSite"); //$NON-NLS-1$
    static final Msg DELETE_SITE = new Msg("EditSitePane.DeleteSite"); //$NON-NLS-1$

    static final Msg ERROR = new Msg("PassageSelectionPane.Error"); //$NON-NLS-1$
    static final Msg SUMMARY = new Msg("PassageSelectionPane.Summary"); //$NON-NLS-1$

    static final Msg SEARCH_PHRASE = new Msg("AdvancedSearchPane.Phrase"); //$NON-NLS-1$
    static final Msg SEARCH_GO = new Msg("AdvancedSearchPane.Go"); //$NON-NLS-1$
    static final Msg SEARCH_SELECT = new Msg("AdvancedSearchPane.Select"); //$NON-NLS-1$
    static final Msg SEARCH_RESTRICT = new Msg("AdvancedSearchPane.Restrict"); //$NON-NLS-1$
    static final Msg SEARCH_HEADING = new Msg("AdvancedSearchPane.Heading"); //$NON-NLS-1$
    static final Msg SEARCH_EXCLUDES = new Msg("AdvancedSearchPane.Excludes"); //$NON-NLS-1$
    static final Msg SEARCH_INCLUDES = new Msg("AdvancedSearchPane.Includes"); //$NON-NLS-1$

    static final Msg SELECT_GO = new Msg("KeySelectPane.Go"); //$NON-NLS-1$
    static final Msg SELECT_KEYS = new Msg("KeySelectPane.Keys"); //$NON-NLS-1$
    static final Msg SELECT_MATCH = new Msg("KeySelectPane.Match"); //$NON-NLS-1$
    static final Msg SELECT_ADVANCED = new Msg("KeySelectPane.Advanced"); //$NON-NLS-1$
    static final Msg SELECT_HELP = new Msg("KeySelectPane.Help"); //$NON-NLS-1$
    static final Msg SELECT_BIBLE = new Msg("KeySelectPane.Bible"); //$NON-NLS-1$
    static final Msg SELECT_DISPLAY = new Msg("KeySelectPane.Display"); //$NON-NLS-1$
    static final Msg SELECT_SEARCH = new Msg("KeySelectPane.Search"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
