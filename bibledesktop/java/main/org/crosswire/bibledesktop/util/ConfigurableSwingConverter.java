package org.crosswire.bibledesktop.util;

import java.awt.Font;
import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;

import javax.xml.transform.TransformerException;

import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.util.StringUtil;
import org.crosswire.common.util.URLFilter;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.TransformingSAXEventProvider;

/**
 * Turn XML from a Bible into HTML according to a Display style.
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
public class ConfigurableSwingConverter implements Converter
{
    /**
     * Get an array of the available style names for a given subject.
     * Different subjects are available for different contexts. For
     * example - for insertion into a web page we might want to use a set
     * that had complex HTML, or IE/NS specific HTML, where as a JFC
     * HTMLDocument needs simpler HTML - and special tags like the
     * starting &lt;HTML> tags.
     * <p>If the protocol of the URL of the current directory is not file
     * then we can't use File.list to get the contents of the directory.
     * This will happen if this is being run as an applet. When we start
     * doing that then we will need to think up something smarter here.
     * Until then we just return a zero length array.
     * @return An array of available style names
     */
    public String[] getStyles()
    {
        try
        {
            String search = "xsl/cswing/" + NetUtil.INDEX_FILE; //$NON-NLS-1$
            URL index = ResourceUtil.getResource(search);
            return NetUtil.listByIndexFile(index, new URLFilter()
            {
                public boolean accept(String name)
                {
                    return name.endsWith(FileUtil.EXTENSION_XSLT);
                }
            });
        }
        catch (IOException ex)
        {
            return new String[0];
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.Converter#convert(org.crosswire.common.xml.SAXEventProvider)
     */
    public SAXEventProvider convert(SAXEventProvider xmlsep) throws TransformerException
    {
        try
        {
            String path = "xsl/cswing/" + style; //$NON-NLS-1$
            URL xslurl = ResourceUtil.getResource(path);

            TransformingSAXEventProvider tsep = new TransformingSAXEventProvider(xslurl, xmlsep);
            // We used to do:
            // tsep.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            // however for various reasons, now we don't but nothing seems to be broken ...
            tsep.setParameter("font", font); //$NON-NLS-1$

            return tsep;
        }
        catch (MissingResourceException ex)
        {
            throw new TransformerException(ex);
        }
    }

    /**
     * Accessor for the stylesheet we are transforming using
     */
    public static String getFont()
    {
        return font;
    }

    /**
     * Accessor for the stylesheet we are transforming using
     */
    public static Font toFont()
    {
        String[] fontSpec = StringUtil.split(font, ","); //$NON-NLS-1$
        int fontStyle = Integer.parseInt(fontSpec[1]);
        int fontSize = Integer.parseInt(fontSpec[2]);
        Font newFont = new Font(fontSpec[0], fontStyle, fontSize);
        return newFont;
    }

    /**
     * Accessor for the stylesheet we are transforming using
     */
    public static void setFont(String font)
    {
        ConfigurableSwingConverter.font = font;
    }

    /**
     * Accessor for the stylesheet we are transforming using
     */
    public static String getResourceName()
    {
        return style;
    }

    /**
     * Accessor for the stylesheet we are transforming using
     */
    public static void setResourceName(String style)
    {
        ConfigurableSwingConverter.style = style;
    }

    /**
     * The font to be used in OSIS->HTML generation
     */
    private static String font = "Serif,0,14"; //$NON-NLS-1$

    /**
     * The stylesheet we are transforming using
     */
    private static String style = "simple.xsl"; //$NON-NLS-1$
}
