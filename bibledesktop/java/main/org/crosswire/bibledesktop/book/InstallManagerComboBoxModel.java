package org.crosswire.bibledesktop.book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.InstallerEvent;
import org.crosswire.jsword.book.install.InstallerListener;

/**
 * A ListModel for a JList that uses the list of Installers given by the
 * InstallManager.
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
public class InstallManagerComboBoxModel extends AbstractListModel implements ComboBoxModel
{
	/**
     * Simple ctor
     */
    public InstallManagerComboBoxModel(InstallManager imanager)
    {
        this.imanager = imanager;

        update(null);
        selection = getElementAt(0);

        imanager.addInstallerListener(new CustomInstallerListener());
    }

    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#getSelectedItem()
     */
    public Object getSelectedItem()
    {
        return selection;
    }

    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
     */
    public void setSelectedItem(Object selection)
    {
        this.selection = selection;
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize()
    {
        return names.size();
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public final Object getElementAt(int index)
    {
        return names.get(index);
    }

    /**
     * Listens to the InstallManager for Installer changes
     */
    private class CustomInstallerListener implements InstallerListener
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.install.InstallerListener#installerAdded(org.crosswire.jsword.book.install.InstallerEvent)
         */
        public void installerAdded(InstallerEvent ev)
        {
            update(ev);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.install.InstallerListener#installerRemoved(org.crosswire.jsword.book.install.InstallerEvent)
         */
        public void installerRemoved(InstallerEvent ev)
        {
            update(ev);
        }
    }

    /**
     * Simple way to avoid eclipse private/protected warning
     */
    protected final void update(InstallerEvent ev)
    {
        int oldmax = names.size();

        names.clear();
        names.addAll(imanager.getInstallers().keySet());
        Collections.sort(names);

        if (ev != null)
        {
            fireContentsChanged(ev.getSource(), 0, oldmax);
        }
    }

    /**
     * The currently selected object
     */
    private Object selection;

    /**
     * A cache of the names in the Install Manager
     */
    private List names = new ArrayList();

    /**
     * The install manager that we are representing
     */
    private InstallManager imanager;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256725082729756980L;
}
