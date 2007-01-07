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
 * ID: $Id: TransformingSAXEventProviderConverter.java 1083 2006-04-18 18:13:36Z dmsmith $
 */
package org.crosswire.common.xml;

import java.net.URL;

/**
 * An implementation of Converter that uses a TransformingSAXEventProvider to
 * transform one SAXEventProvider into another SAXEventProvider using XSL.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class TransformingSAXEventProviderConverter implements Converter
{
    /**
     * Simple ctor
     * @param xslurl The url of the stylesheet
     */
    public TransformingSAXEventProviderConverter(URL xslurl)
    {
        this.xslurl = xslurl;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.xml.Converter#convert(org.crosswire.common.xml.SAXEventProvider)
     */
    public SAXEventProvider convert(SAXEventProvider provider)
    {
        return new TransformingSAXEventProvider(xslurl, provider);
    }

    /**
     * The URL of the stylesheet
     */
    private URL xslurl;
}