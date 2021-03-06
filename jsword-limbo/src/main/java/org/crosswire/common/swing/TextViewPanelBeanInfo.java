package org.crosswire.common.swing;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import org.crosswire.common.util.Logger;

/**
 * BeanInfo for the TextViewer. This was mostly generate using BeansExpress.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class TextViewPanelBeanInfo extends SimpleBeanInfo {
    // DEAD(DM): This class is not used. Find a use for it or delete it.
    /**
     * Info about the extra properties we provide
     * 
     * @return an array of property descriptors
     */
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            // The header property
            PropertyDescriptor header = new PropertyDescriptor("header", TextViewPanel.class, "getHeader", "setHeader");
            header.setDisplayName("Header");
            header.setShortDescription("Header");
            header.setBound(true);

            // The main text property
            PropertyDescriptor text = new PropertyDescriptor("text", TextViewPanel.class, "getText", "setText");
            text.setDisplayName("Text");
            text.setShortDescription("Text");
            text.setBound(true);

            return new PropertyDescriptor[] {
                    header, text,
            };
        } catch (IntrospectionException ex) {
            log.info("Failure", ex);
            return null;
        }
    }

    /**
     * Get additional information from the superclass, in this case JPanel
     */
    @Override
    public BeanInfo[] getAdditionalBeanInfo() {
        Class superclass = TextViewPanel.class.getSuperclass();
        try {
            BeanInfo superBeanInfo = Introspector.getBeanInfo(superclass);
            return new BeanInfo[] {
                superBeanInfo
            };
        } catch (IntrospectionException ex) {
            log.info("Failure", ex);
            return null;
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(TextViewPanelBeanInfo.class);
}
