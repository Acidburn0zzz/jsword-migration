package org.crosswire.jsword.book;

import java.util.Iterator;
import java.util.List;

import org.crosswire.common.util.Logger;

/**
 * Handles the current default Books.
 * 
 * <p>This is used whenever the user works with one Book at a time and many
 * parts of the system want to know what the current is.
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
public class Defaults
{
    /**
     * Prevent construction
     */
    private Defaults()
    {
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Defaults.class);

    /**
     * Has the default Bible been manually set or are we picking the fastest
     * as the default?
     */
    private static boolean autobdeft = true;

    /**
     * The default Bible
     */
    private static BookMetaData bdeft = null;

    /**
     * Has the default Commentary been manually set or are we picking the fastest
     * as the default?
     */
    private static boolean autocdeft = true;

    /**
     * The default Commentary
     */
    private static BookMetaData cdeft = null;

    /**
     * Has the default Dictionary been manually set or are we picking the fastest
     * as the default?
     */
    private static boolean autoddeft = true;

    /**
     * The default Dictionary
     */
    private static BookMetaData ddeft = null;

    /**
     * Set the default Bible. The new name must be equal() to a string
     * returned from getBibleNames. (if does not need to be == however)
     * A BookException results if you get it wrong.
     * @param bmd The version to use as default.
     */
    public static void setBibleMetaData(BookMetaData bmd)
    {
        autobdeft = false;
        bdeft = bmd;
    }

    /**
     * UnSet the current default Bible and attempt to appoint another.
     */
    protected static void unsetBibleMetaData()
    {
        autobdeft = true;
        bdeft = null;

        checkAllPreferable();
    }

    /**
     * Get the current default Bible or null if there are no Bibles.
     * @return the current default version
     */
    public static BookMetaData getBibleMetaData()
    {
        return bdeft;
    }

    /**
     * This method is identical to <code>getBibleMetaData().getFullName()</code>
     * and is only used by Config which works best with strings under reflection.
     */
    public static String getBibleByName()
    {
        if (bdeft == null)
        {
            return null;
        }

        return bdeft.getFullName();
    }

    /**
     * Trawl through all the known Bibles looking for the one closest to
     * the given name.
     * <p>This method is for use with config scripts and other things that
     * <b>need</b> to work with Strings. The preferred method is to use
     * BookMetaData objects.
     * <p>This method is picky in that it only matches when the driver and the
     * version are the same. The user (probably) only cares about the version
     * though, and so might be dissapointed when we fail to match AV (FooDriver)
     * against AV (BarDriver).
     * @param name The version to use as default.
     * @exception BookException If the name is not valid
     */
    public static void setBibleByName(String name) throws BookException
    {
        if (name == null || name.length() == 0)
        {
            log.warn("Attempt to set empty Bible as default. Ignoring"); //$NON-NLS-1$
            return;
        }

        List lbmds = Books.installed().getBookMetaDatas(BookFilters.getBibles());
        for (Iterator it = lbmds.iterator(); it.hasNext(); )
        {
            BookMetaData bmd = (BookMetaData) it.next();
            String tname = bmd.getFullName();
            if (tname.equals(name))
            {
                setBibleMetaData(bmd);
                return;
            }
        }
    
        throw new BookException(Msg.BIBLE_NOTFOUND, new Object[] { name });
    }

    /**
     * Set the default Commentary. The new name must be equal() to a string
     * returned from getCommentaryNames. (if does not need to be == however)
     * A BookException results if you get it wrong.
     * @param cmd The version to use as default.
     */
    public static void setCommentaryMetaData(BookMetaData cmd)
    {
        autocdeft = false;
        cdeft = cmd;
    }

    /**
     * UnSet the current default Commentary and attempt to appoint another.
     */
    protected static void unsetCommentaryMetaData()
    {
        autocdeft = true;
        cdeft = null;

        checkAllPreferable();
    }

    /**
     * Get the current default Commentary or null if none exist.
     * @return the current default version
     */
    public static BookMetaData getCommentaryMetaData()
    {
        return cdeft;
    }

    /**
     * This method is identical to <code>getCommentaryMetaData().getFullName()</code>
     * and is only used by Config which works best with strings under reflection.
     * <p>Generally <code>getCommentaryByName().getFullName()</code> is a better
     * way of getting what you want.
     */
    public static String getCommentaryByName()
    {
        if (cdeft == null)
        {
            return null;
        }

        return cdeft.getFullName();
    }

    /**
     * Trawl through all the known Commentary looking for the one closest to
     * the given name.
     * <p>This method is for use with config scripts and other things that
     * <b>need</b> to work with Strings. The preferred method is to use
     * BookMetaData objects.
     * <p>This method is picky in that it only matches when the driver and the
     * version are the same. The user (probably) only cares about the version
     * though, and so might be dissapointed when we fail to match AV (FooDriver)
     * against AV (BarDriver).
     * @param name The version to use as default.
     * @exception BookException If the name is not valid
     */
    public static void setCommentaryByName(String name) throws BookException
    {
        if (name == null || name.length() == 0)
        {
            log.warn("Attempt to set empty Commentary as default. Ignoring"); //$NON-NLS-1$
            return;
        }

        List lbmds = Books.installed().getBookMetaDatas(BookFilters.getCommentaries());
        for (Iterator it = lbmds.iterator(); it.hasNext(); )
        {
            BookMetaData cmd = (BookMetaData) it.next();
            String tname = cmd.getFullName();
            if (tname.equals(name))
            {
                setCommentaryMetaData(cmd);
                return;
            }
        }
    
        throw new BookException(Msg.COMMENTARY_NOTFOUND, new Object[] { name });
    }

    /**
     * Set the default Dictionary. The new name must be equal() to a string
     * returned from getDictionaryNames. (if does not need to be == however)
     * A BookException results if you get it wrong.
     * @param dmd The version to use as default.
     */
    public static void setDictionaryMetaData(BookMetaData dmd)
    {
        autoddeft = false;
        ddeft = dmd;
    }

    /**
     * UnSet the current default Dictionary and attempt to appoint another.
     */
    protected static void unsetDictionaryMetaData()
    {
        autoddeft = true;
        ddeft = null;

        checkAllPreferable();
    }

    /**
     * Get the current default Dictionary or null if none exist.
     * @return the current default version
     */
    public static BookMetaData getDictionaryMetaData()
    {
        return ddeft;
    }

    /**
     * This method is identical to <code>getDictionaryMetaData().getFullName()</code>
     * and is only used by Config which works best with strings under reflection.
     * <p>Generally <code>getDictionaryByName().getFullName()</code> is a better
     * way of getting what you want.
     */
    public static String getDictionaryByName()
    {
        if (ddeft == null)
        {
            return null;
        }

        return ddeft.getFullName();
    }

    /**
     * Trawl through all the known Dictionaries looking for the one closest to
     * the given name.
     * <p>This method is for use with config scripts and other things that
     * <b>need</b> to work with Strings. The preferred method is to use
     * BookMetaData objects.
     * <p>This method is picky in that it only matches when the driver and the
     * version are the same. The user (probably) only cares about the version
     * though, and so might be dissapointed when we fail to match AV (FooDriver)
     * against AV (BarDriver).
     * @param name The version to use as default.
     * @exception BookException If the name is not valid
     */
    public static void setDictionaryByName(String name) throws BookException
    {
        if (name == null || name.length() == 0)
        {
            log.warn("Attempt to set empty Dictionary as default. Ignoring"); //$NON-NLS-1$
            return;
        }

        List lbmds = Books.installed().getBookMetaDatas(BookFilters.getDictionaries());
        for (Iterator it = lbmds.iterator(); it.hasNext(); )
        {
            BookMetaData dmd = (BookMetaData) it.next();
            String tname = dmd.getFullName();
            if (tname.equals(name))
            {
                setDictionaryMetaData(dmd);
                return;
            }
        }
    
        throw new BookException(Msg.DICTIONARY_NOTFOUND, new Object[] { name });
    }

    /**
     * Go through all of the current books checking to see if we need to replace
     * the current defaults with one of these.
     */
    protected static void checkAllPreferable()
    {
        List bmds = Books.installed().getBookMetaDatas();
        for (Iterator it = bmds.iterator(); it.hasNext(); )
        {
            BookMetaData bmd = (BookMetaData) it.next();
            checkPreferable(bmd);
        }
    }

    /**
     * Should this Bible become the default?
     */
    protected static void checkPreferable(BookMetaData bmd)
    {
        assert bmd != null;

        if (bmd.getType().equals(BookType.BIBLE))
        {
            // Do we even think about replacing the default Bible?
            if (autobdeft || bdeft == null)
            {
                // If there is no default or this is faster
                if (bdeft == null || bmd.getSpeed() > bdeft.getSpeed())
                {
                    bdeft = bmd;
                    autobdeft = true;
                    log.debug("setting as default bible since speed=" + bdeft.getSpeed()); //$NON-NLS-1$
                }
            }
        }
        else if (bmd.getType().equals(BookType.COMMENTARY))
        {
            // Do we even think about replacing the default Bible?
            if (autocdeft || cdeft == null)
            {
                // If there is no default or this is faster
                if (cdeft == null || bmd.getSpeed() > cdeft.getSpeed())
                {
                    cdeft = bmd;
                    autocdeft = true;
                    log.debug("setting as default commentary since speed=" + cdeft.getSpeed()); //$NON-NLS-1$
                }
            }
        }
        else if (bmd.getType().equals(BookType.DICTIONARY))
        {
            // Do we even think about replacing the default Bible?
            if (autoddeft || ddeft == null)
            {
                // If there is no default or this is faster
                if (ddeft == null || bmd.getSpeed() > ddeft.getSpeed())
                {
                    ddeft = bmd;
                    autoddeft = true;
                    log.debug("setting as default dictionary since speed=" + ddeft.getSpeed()); //$NON-NLS-1$
                }
            }
        }
    }

    /**
     * Register with Books so we know how to provide valid defaults
     */
    static
    {
        Books.installed().addBooksListener(new DefaultsBookListener());
        checkAllPreferable();
    }

    /**
     * To keep us up to date with changes in the available Books
     */
    private static class DefaultsBookListener implements BooksListener
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BooksListener#bookAdded(org.crosswire.jsword.book.BooksEvent)
         */
        public void bookAdded(BooksEvent ev)
        {
            BookMetaData bmd = ev.getBookMetaData(); 
            checkPreferable(bmd);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BooksListener#bookRemoved(org.crosswire.jsword.book.BooksEvent)
         */
        public void bookRemoved(BooksEvent ev)
        {
            BookMetaData bmd = ev.getBookMetaData(); 

            // Was this a default?
            if (getBibleMetaData().equals(bmd))
            {
                unsetBibleMetaData();
            }

            if (getCommentaryMetaData().equals(bmd))
            {
                unsetCommentaryMetaData();
            }

            if (getDictionaryMetaData().equals(bmd))
            {
                unsetDictionaryMetaData();
            }
        }
    }
}
