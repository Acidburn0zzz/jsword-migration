
package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BibleDriverManager;
import org.crosswire.jsword.book.BibleMetaData;
import org.crosswire.jsword.book.Bibles;
import org.crosswire.jsword.book.basic.AbstractBibleDriver;

/**
 * This represents all of the SwordBibles.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class SwordBibleDriver extends AbstractBibleDriver
{
    /**
     * Some basic driver initialization
     */
    private SwordBibleDriver() throws MalformedURLException
    {
    }

    /**
     * Some basic info about who we are
     * @param A short identifing string
     */
    public String getDriverName()
    {
        return "Sword";
    }

    /**
     * @see org.crosswire.jsword.book.BibleDriver#getBibles()
     */
    public BibleMetaData[] getBibles()
    {
        if (dir == null)
        {
            return new BibleMetaData[0];
        }
        try
        {
            // load each config withing mods.d, discard those which are not bibles, return names of remaining
            URL mods = NetUtil.lengthenURL(dir, "mods.d");
            if (!NetUtil.isDirectory(mods))
                return new BibleMetaData[0];

            File modsFile = new File(mods.getFile());
            String[] filenames = modsFile.list(new CustomFilenameFilter());
            SwordConfig[] configs = new SwordConfig[filenames.length];
            List valid = new ArrayList();
            for (int i = 0; i < filenames.length; i++)
            {
                String biblename = filenames[i].substring(0, filenames[i].indexOf(".conf"));
                configs[i] = new SwordConfig(new File(modsFile, filenames[i]).toURL(), biblename);

                // check to see if it's a bible
                if (isBible(configs[i]))
                {
                    BibleMetaData bmd = new SwordBibleMetaData(biblename);
                    valid.add(bmd);
                    configCache.put(bmd, configs[i]);
                }
            }

            return (BibleMetaData[]) valid.toArray(new BibleMetaData[valid.size()]);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return new BibleMetaData[0];
        }
    }

    /**
     * Method isBible.
     * @param swordConfig
     * @return boolean
     */
    private boolean isBible(SwordConfig swordConfig)
    {
        if (swordConfig.getModDrv() == SwordConstants.DRIVER_RAW_TEXT)
            return true;

        if (swordConfig.getModDrv() == SwordConstants.DRIVER_Z_TEXT)
            return true;

        return false;
    }

    /**
     * Accessor for the Sword directory
     * @param sword_dir The new Sword directory
     */
    public static void setSwordDir(String sword_dir) throws MalformedURLException
    {
        // Just accept that we're not supposed to work ...
        if (sword_dir == null || sword_dir.trim().length() == 0)
        {
            driver.dir = null;
            log.info("No sword dir set.");
            return;
        }

        URL dir_temp = new URL("file:" + sword_dir);

        if (!NetUtil.isDirectory(dir_temp))
            throw new MalformedURLException("No sword source found under " + sword_dir);

        driver.dir = dir_temp;
    }

    /**
     * Accessor for the Sword directory
     * @return The new Sword directory
     */
    public static String getSwordDir()
    {
        if (driver.dir == null)
            return "";

        return driver.dir.toExternalForm().substring(5);
    }

    /** config cache */
    protected Map configCache = new HashMap();

    /** The directory URL */
    protected URL dir;

    /** The singleton driver */
    protected static SwordBibleDriver driver;

    /** The log stream */
    protected static Logger log = Logger.getLogger(SwordBibleDriver.class);

    /**
     * Register ourselves with the Driver Manager
     */
    static
    {
        try
        {
            driver = new SwordBibleDriver();
            BibleMetaData[] bmds = driver.getBibles();
            for (int i=0; i<bmds.length; i++)
            {
                Bibles.addBible(bmds[i]);
            }

            BibleDriverManager.registerDriver(driver);
        }
        catch (Exception ex)
        {
            Reporter.informUser(SwordBibleDriver.class, ex);
        }
    }

    /**
     * Check that the directories in the version directory really
     * represent versions.
     */
    static class CustomFilenameFilter implements FilenameFilter
    {
        public boolean accept(File parent, String name)
        {
            if (name.endsWith(".conf") && !name.startsWith("globals."))
                return true;
            else
                return false;
        }
    }
}
