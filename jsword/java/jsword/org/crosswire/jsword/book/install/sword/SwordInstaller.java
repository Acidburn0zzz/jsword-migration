package org.crosswire.jsword.book.install.sword;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.book.sword.ModuleType;
import org.crosswire.jsword.book.sword.SwordBookDriver;
import org.crosswire.jsword.book.sword.SwordConfig;
import org.crosswire.jsword.util.Project;

import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;

/**
 * An implementation of Installer for reading data from Sword FTP sites.
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
public class SwordInstaller implements Installer, Comparable
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#getURL()
     */
    public String getURL()
    {
        return "sword:"+username+":"+password+"@"+host+directory;
    }

    /**
     * Like getURL() except that we skip the password for display purposes.
     * @see SwordInstaller#getURL()
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "sword:"+username+"@"+host+directory;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#getIndex()
     */
    public List getIndex()
    {
        try
        {
            loadCachedIndex();

            return new ArrayList(entries.keySet());
        }
        catch (InstallException ex)
        {
            log.error("Failed to reload cached index file", ex);
            return new ArrayList();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#install(java.lang.String)
     */
    public void install(String entry) throws InstallException
    {
        try
        {
            SwordConfig config = (SwordConfig) entries.get(entry);
            ModuleType type = config.getModDrv();
            String modpath = type.getInstallDirectory();
            String destname = modpath + "/" + config.getInternalName();

            File dldir = SwordBookDriver.getDownloadDir();
            File moddir = new File(dldir, "modules");
            File fulldir = new File(moddir, destname);
            fulldir.mkdirs();
            URL desturl = new URL("file", null, fulldir.getAbsolutePath());

            downloadAll(host, USERNAME, PASSWORD, directory+"/modules/"+destname, desturl);

            File confdir = new File(dldir, "mods.d");
            confdir.mkdirs();
            File conf = new File(confdir, config.getInternalName()+".conf");
            URL configurl = new URL("file", null, conf.getAbsolutePath());
            config.save(configurl);
        }
        catch (IOException ex)
        {
            throw new InstallException(Msg.URL_FAILED, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#reloadIndex()
     */
    public List reloadIndex() throws InstallException
    {
        cacheRemoteFile();
        loadCachedIndex();

        return new ArrayList(entries.keySet());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.install.Installer#getEntry(java.lang.String)
     */
    public Properties getEntry(String entry)
    {
        SwordConfig config = (SwordConfig) entries.get(entry);
        Properties prop = config.getProperties();
        return prop;
    }

    /**
     * Load the index file from FTP and parse it
     */
    private void cacheRemoteFile() throws InstallException
    {
        URL scratchfile = getCachedIndexFile();
        download(host, USERNAME, PASSWORD, directory, FILE_LIST_GZ, scratchfile);
    }

    /**
     * Load the cached index file into memory
     */
    private void loadCachedIndex() throws InstallException
    {
        try
        {
            entries.clear();

            URL cache = getCachedIndexFile();
            if (!NetUtil.isFile(cache))
            {
                log.info("Missing cache file: "+cache.toExternalForm());
                return;
            }

            InputStream in = cache.openStream();
            GZIPInputStream gin = new GZIPInputStream(in);
            TarInputStream tin = new TarInputStream(gin);

            while (true)
            {
                TarEntry entry = tin.getNextEntry();
                if (entry == null)
                {
                    break;
                }

                if (!entry.isDirectory())
                {
                    int size = (int) entry.getSize();
                    byte[] buffer = new byte[size];
                    tin.read(buffer);

                    String internal = entry.getName();
                    if (internal.endsWith(".conf"))
                    {
                        internal = internal.substring(0, internal.length() - 5);
                    }
                    if (internal.startsWith("mods.d/"))
                    {
                        internal = internal.substring(7);
                    }

                    Reader rin = new InputStreamReader(new ByteArrayInputStream(buffer));
                    SwordConfig config = new SwordConfig(rin, internal);
                    String desc = config.getDescription();
                    entries.put(desc, config);
                }
            }
            
            tin.close();
            gin.close();
            in.close();
        }
        catch (IOException ex)
        {
            throw new InstallException(Msg.CACHE_ERROR, ex);
        }
    }

    /**
     * The URL for the cached index file for this installer
     */
    private URL getCachedIndexFile() throws InstallException
    {
        try
        {
            String encoded = host + directory.replace('/', '_');
            URL scratchdir = Project.instance().getTempScratchSpace("download-" + encoded);
            return NetUtil.lengthenURL(scratchdir, FILE_LIST_GZ);
        }
        catch (IOException ex)
        {
            throw new InstallException(Msg.URL_FAILED, ex);
        }
    }

    /**
     * Utility to download a file by FTP from a remote site
     * @param site The place to download from
     * @param user The user that does the downloading
     * @param password The password of the above user
     * @param dir The directory from which to download the file
     * @param file The file to download
     * @throws InstallException
     */
    private static void download(String site, String user, String password, String dir, String file, URL dest) throws InstallException
    {
        FTPClient ftp = new FTPClient();

        try
        {
            ftpInit(ftp, site, user, password, dir);

            // Download the index file
            OutputStream out = NetUtil.getOutputStream(dest);

            ftp.retrieveFile(file, out);
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply))
            {
                String text = ftp.getReplyString();
                throw new InstallException(Msg.DOWNLOAD_REFUSED, new Object[] { FILE_LIST_GZ, new Integer(reply), text });
            }
            out.close();
        }
        catch (IOException ex)
        {
            throw new InstallException(Msg.UNKNOWN_ERROR, ex);
        }
        finally
        {
            disconnect(ftp);
        }
    }

    /**
     * Utility to download a file by FTP from a remote site
     * @param site The place to download from
     * @param user The user that does the downloading
     * @param password The password of the above user
     * @param dir The directory from which to download the file
     * @throws InstallException
     */
    private static void downloadAll(String site, String user, String password, String dir, URL destdir) throws InstallException
    {
        FTPClient ftp = new FTPClient();

        try
        {
            ftpInit(ftp, site, user, password, dir);

            downloadContents(destdir, ftp);
        }
        catch (InstallException ex)
        {
            throw ex;
        }
        catch (IOException ex)
        {
            throw new InstallException(Msg.UNKNOWN_ERROR, ex);
        }
        finally
        {
            disconnect(ftp);
        }
    }

    /**
     * Recursively download the contents of the current ftp directory
     * to the given url
     */
    private static void downloadContents(URL destdir, FTPClient ftp) throws IOException, InstallException
    {
        FTPFile[] files = ftp.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            String name = files[i].getName();
            URL child = NetUtil.lengthenURL(destdir, name);

            if (files[i].isFile())
            {
                OutputStream out = NetUtil.getOutputStream(child);

                ftp.retrieveFile(name, out);

                int reply = ftp.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply))
                {
                    String text = ftp.getReplyString();
                    throw new InstallException(Msg.DOWNLOAD_REFUSED, new Object[] { FILE_LIST_GZ, new Integer(reply), text });
                }
                out.close();
            }
            else
            {
                downloadContents(child, ftp);
            }
        }
    }

    /**
     * Simple tway to connect to a remote site in preparation for a file listing
     * or a download.
     */
    private static void ftpInit(FTPClient ftp, String site, String user, String password, String dir) throws IOException, InstallException
    {
        log.info("Connecting to site=" + site + " dir=" + dir);

        // First connect
        ftp.connect(site);

        log.info(ftp.getReplyString());
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply))
        {
            String text = ftp.getReplyString();
            throw new InstallException(Msg.CONNECT_REFUSED, new Object[] { site, new Integer(reply), text });
        }

        // Authenticate
        ftp.login(user, password);

        log.info(ftp.getReplyString());
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply))
        {
            String text = ftp.getReplyString();
            throw new InstallException(Msg.AUTH_REFUSED, new Object[] { user, new Integer(reply), text });
        }

        // Change directory
        ftp.changeWorkingDirectory(dir);

        log.info(ftp.getReplyString());
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply))
        {
            String text = ftp.getReplyString();
            throw new InstallException(Msg.CWD_REFUSED, new Object[] { dir, new Integer(reply), text });
        }

        ftp.setFileType(FTP.BINARY_FILE_TYPE);
    }

    /**
     * Silently close an ftp connection, ignoring any exceptions
     */
    private static void disconnect(FTPClient ftp)
    {
        if (ftp.isConnected())
        {
            try
            {
                ftp.disconnect();
            }
            catch (IOException ex2)
            {
                log.error("disconnect error", ex2);
            }
        }
    }

    /**
     * @return Returns the directory.
     */
    public String getDirectory()
    {
        return directory;
    }

    /**
     * @param directory The directory to set.
     */
    public void setDirectory(String directory)
    {
        this.directory = directory;
    }

    /**
     * @return Returns the host.
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @param host The host to set.
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return Returns the username.
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @param username The username to set.
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object)
    {
        if (!(object instanceof SwordInstaller))
        {
            return false;
        }
        SwordInstaller that = (SwordInstaller) object;

        if (!equals(this.host, that.host))
        {
            return false;
        }

        if (!equals(this.directory, that.directory))
        {
            return false;
        }

        if (!equals(this.password, that.password))
        {
            return false;
        }

        if (!equals(this.username, that.username))
        {
            return false;
        }

        return true;
    }

    /**
     * Quick utility to check to see if 2 (potentially null) strings are equal
     */
    private boolean equals(String string1, String string2)
    {
        if (string1 == null)
        {
            return string2 == null;
        }
        return string1.equals(string2);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return host.hashCode() + directory.hashCode() + username.hashCode() + password.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object object)
    {
        SwordInstaller myClass = (SwordInstaller) object;

        return new CompareToBuilder()
            .append(this.host, myClass.host)
            .append(this.directory, myClass.directory)
            .toComparison();
    }
    
    /**
     * The remote hostname.
     */
    private String host;

    /**
     * The remote username for a valid account on the <code>host</code>.
     */
    private String username = "anonymous";

    /**
     * The password to go with <code>username</code>.
     */
    private String password = "jsword@crosswire.com";

    /**
     * The directory containing modules on the <code>host</code>.
     */
    private String directory = "/";

    /**
     * A map of the entries in this download area
     */
    private Map entries = new HashMap();

    /**
     * The default anon username
     */
    private static final String USERNAME = "anonymous";

    /**
     * The default anon password
     */
    private static final String PASSWORD = "anon@anon.com";

    /**
     * The sword index file
     */
    private static final String FILE_LIST_GZ = "mods.d.tar.gz";

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SwordInstaller.class);
}
