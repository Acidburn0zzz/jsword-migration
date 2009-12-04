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
package org.crosswire.common.util;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ThreadUtilTest extends TestCase {
    public ThreadUtilTest(String s) {
        super(s);
    }

    String NEWLINE = System.getProperty("line.separator", "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$

    /* @Override */
    protected void setUp() throws Exception {
    }

    /* @Override */
    protected void tearDown() throws Exception {
    }

    public void testFindRoot() {
        assertTrue(ThreadUtil.findRoot() != null);
    }

    public void testGetListing() {
        /*
         * String[] result = ThreadUtil.getListing(); String result2 =
         * StringUtil.cat(result, NEWLINE);
         */
    }
}
