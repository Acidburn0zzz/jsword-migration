
package org.crosswire.common.swing;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import org.crosswire.common.util.Logger;

/**
 * BeanInfo for the TextViewer. This was mostly generate using
 * BeansExpress.
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
 */
public class TextViewPanelBeanInfo extends SimpleBeanInfo
{
    // DEAD(DM): This class is not used. Find a use for it or delete it.
    /**
    * Info about the extra properties we provide
    * @return an array of property descriptors
    */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        try
        {
            // The header property
            PropertyDescriptor header = new PropertyDescriptor("header", TextViewPanel.class, "getHeader", "setHeader"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            header.setDisplayName("Header"); //$NON-NLS-1$
            header.setShortDescription("Header"); //$NON-NLS-1$
            header.setBound(true);

            // The main text property
            PropertyDescriptor text = new PropertyDescriptor("text", TextViewPanel.class, "getText", "setText"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            text.setDisplayName("Text"); //$NON-NLS-1$
            text.setShortDescription("Text"); //$NON-NLS-1$
            text.setBound(true);

            return new PropertyDescriptor[] { header, text, };
        }
        catch (IntrospectionException ex)
        {
            log.info("Failure", ex); //$NON-NLS-1$
            return null;
        }
    }

    /**
    * Get additional information from the superclass, in this case JPanel
    */
    public BeanInfo[] getAdditionalBeanInfo()
    {
        Class superclass = TextViewPanel.class.getSuperclass();
        try
        {
            BeanInfo superBeanInfo = Introspector.getBeanInfo(superclass);
            return new BeanInfo[] { superBeanInfo };
        }
        catch (IntrospectionException ex)
        {
            log.info("Failure", ex); //$NON-NLS-1$
            return null;
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(TextViewPanelBeanInfo.class);
}
