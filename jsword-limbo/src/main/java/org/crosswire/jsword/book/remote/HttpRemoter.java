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
package org.crosswire.jsword.book.remote;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import org.crosswire.common.util.Logger;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

/**
 * Implement a Remoter using HTTP.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class HttpRemoter implements Remoter {
    /**
     * Create an HttpRemoter with a baseurl to call.
     */
    public HttpRemoter(String baseurl) {
        if (baseurl == null) {
            throw new NullPointerException();
        }

        this.baseurl = baseurl;
    }

    /**
     * A simple name
     */
    public String getRemoterName() {
        return "Remote (HTTP)";
    }

    /**
     * @see Remoter#execute(RemoteMethod)
     */
    public Document execute(RemoteMethod method) throws RemoterException {
        try {
            String query = baseurl + methodToParam(method);
            log.debug("Executing query: " + query);

            URL url = new URL(query);
            InputStream in = url.openStream();
            SAXBuilder builder = new SAXBuilder();

            Document doc = builder.build(in);
            log.debug("Counting children of root element: " + doc.getRootElement().getChildren().size());

            return doc;
        } catch (Exception ex) {
            throw new RemoterException(Msg.REMOTE_FAIL, ex);
        }
    }

    /**
     * Convert a RemoteMethod to a String which we can append to a base url to
     * get a complete URL which will get us the required XML document.
     */
    public static String methodToParam(RemoteMethod method) {
        StringBuffer buffer = new StringBuffer();

        buffer.append("?method=");
        buffer.append(method.getMethodName());

        Iterator it = method.getParameterKeys();
        while (it.hasNext()) {
            // JDK: at 1.3 - need to remove try block and change encoder to say:
            try {
                String key = (String) it.next();
                ParamName param = ParamName.fromString(key);
                if (param != null) {
                    String val = method.getParameter(param);
                    String b64 = URLEncoder.encode(val, "UTF-8");

                    buffer.append('&');
                    buffer.append(key);
                    buffer.append('=');
                    buffer.append(b64);
                }
            } catch (UnsupportedEncodingException ex) {
                assert false : ex;
            }
        }

        return buffer.toString();
    }

    /**
     * The URL that we append to to get valid queries
     */
    private String baseurl;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(HttpRemoter.class);

    /**
     * For use in HttpServletRequests
     */
    public static final String METHOD_KEY = "method";
}
