/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
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
package org.crosswire.jsword.book.install.sword;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.crosswire.common.progress.Progress;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.sword.SwordBookMetaData;

/**
 * An implementation of Installer for reading data from Sword FTP sites.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class FtpSwordInstaller extends AbstractSwordInstaller {
    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.install.Installer#getType()
     */
    public String getType() {
        return "sword-ftp";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.install.Installer#getSize(org.crosswire.jsword
     * .book.Book)
     */
    public int getSize(Book book) {
        // not implemented
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.install.Installer#getURL()
     */
    /* @Override */
    public String getInstallerDefinition() {
        return PROTOCOL_SWORD + "://" + username + ":" + password + "@" + host + packageDirectory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.install.Installer#toRemoveURI(org.crosswire
     * .jsword.book.BookMetaData)
     */
    public URI toRemoteURI(Book book) {
        BookMetaData bmd = book.getBookMetaData();
        if (!(bmd instanceof SwordBookMetaData)) {
            assert false;
            return null;
        }

        SwordBookMetaData sbmd = (SwordBookMetaData) bmd;

        try {
            return new URI(NetUtil.PROTOCOL_FTP, host, packageDirectory + "/" + sbmd.getInitials() + ZIP_SUFFIX, null); 
        } catch (URISyntaxException ex) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.install.sword.AbstractSwordInstaller#download
     * (java.lang.String, java.lang.String, java.net.URI)
     */
    /* @Override */
    protected void download(Progress job, String dir, String file, URI dest) throws InstallException {
        FTPClient ftp = new FTPClient();

        try {
            log.info("Connecting to site=" + host + " dir=" + dir);

            // First connect
            ftp.connect(host);
            Thread.yield();

            log.info(ftp.getReplyString());
            int reply1 = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply1)) {
                String text1 = ftp.getReplyString();
                throw new InstallException(FTPMsg.CONNECT_REFUSED, new Object[] {
                        host, Integer.valueOf(reply1), text1
                });
            }

            // Authenticate
            ftp.login(username, password);
            Thread.yield();

            log.info(ftp.getReplyString());
            reply1 = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply1)) {
                String text2 = ftp.getReplyString();
                throw new InstallException(FTPMsg.AUTH_REFUSED, new Object[] {
                        username, Integer.valueOf(reply1), text2
                });
            }

            // Change directory
            ftp.changeWorkingDirectory(dir);
            Thread.yield();

            log.info(ftp.getReplyString());
            reply1 = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply1)) {
                String text3 = ftp.getReplyString();
                throw new InstallException(FTPMsg.CWD_REFUSED, new Object[] {
                        dir, Integer.valueOf(reply1), text3
                });
            }

            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            Thread.yield();

            // Check the download directory exists
            URI parent = NetUtil.shortenURI(dest, FILE_LIST_GZ);
            NetUtil.makeDirectory(parent);

            // Download the index file
            OutputStream out = NetUtil.getOutputStream(dest);

            ftp.retrieveFile(file, out);
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                String text = ftp.getReplyString();
                throw new InstallException(FTPMsg.DOWNLOAD_REFUSED, new Object[] {
                        FILE_LIST_GZ, Integer.valueOf(reply), text
                });
            }
            out.close();
        } catch (IOException ex) {
            throw new InstallException(Msg.UNKNOWN_ERROR, ex);
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ex2) {
                    log.error("disconnect error", ex2);
                }
            }
        }
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Returns the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Like getURL() except that we skip the password for display purposes.
     * 
     * @see FtpSwordInstaller#getInstallerDefinition()
     * @see java.lang.Object#toString()
     */
    /* @Override */
    public String toString() {
        return getInstallerDefinition();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    /* @Override */
    public boolean equals(Object object) {
        if (!(object instanceof FtpSwordInstaller)) {
            return false;
        }
        FtpSwordInstaller that = (FtpSwordInstaller) object;

        if (!super.equals(that)) {
            return false;
        }

        if (!equals(this.password, that.password)) {
            return false;
        }

        if (!equals(this.username, that.username)) {
            return false;
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    /* @Override */
    public int hashCode() {
        return super.hashCode() + username.hashCode() + password.hashCode();
    }

    /**
     * The remote username for a valid account on the <code>host</code>.
     */
    private String username = "anonymous";

    /**
     * The password to go with <code>username</code>.
     */
    private String password = "jsword@crosswire.com";

    /**
     * We need to be ablee to provide a URL as part of the API
     */
    private static final String PROTOCOL_SWORD = "sword-ftp";

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(FtpSwordInstaller.class);
}
