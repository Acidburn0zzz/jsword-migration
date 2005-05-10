/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $ID$
 */
package org.crosswire.bibledesktop.book.install;

import java.awt.Component;

import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;

import org.crosswire.common.swing.AntiAliasedTextPane;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.JDOMSAXEventProvider;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.TransformingSAXEventProvider;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.util.ConverterFactory;

/**
 * A JDK JTextPane implementation of an OSIS displayer.
 *
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class TextPaneBookMetaDataDisplay
{
    /**
     * Simple ctor
     */
    public TextPaneBookMetaDataDisplay()
    {
        converter = ConverterFactory.getConverter();
        txtView = new AntiAliasedTextPane();
        txtView.setEditable(false);
        txtView.setEditorKit(new HTMLEditorKit());
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.Book, org.crosswire.jsword.passage.Key)
     */
    public void setBook(Book book)
    {
        if (book == null)
        {
            txtView.setText(""); //$NON-NLS-1$
            return;
        }

        try
        {

            SAXEventProvider osissep = new JDOMSAXEventProvider(book.toOSIS());
            TransformingSAXEventProvider htmlsep = (TransformingSAXEventProvider) converter.convert(osissep);
            String text = XMLUtil.writeToString(htmlsep);

            txtView.setText(text);
            txtView.select(0, 0);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Accessor for the Swing component
     */
    public Component getComponent()
    {
        return txtView;
    }

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(TextPaneBookMetaDataDisplay.class);

    /**
     * To convert OSIS to HTML
     */
    private Converter converter;

    /**
     * The display component
     */
    private JTextPane txtView;

}
