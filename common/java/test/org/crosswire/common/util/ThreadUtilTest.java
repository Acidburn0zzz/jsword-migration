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
package org.crosswire.common.util;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ThreadUtilTest extends TestCase
{
    public ThreadUtilTest(String s)
    {
        super(s);
    }

    String NEWLINE = System.getProperty("line.separator", "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public void testFindRoot() throws Exception
    {
        assertTrue(ThreadUtil.findRoot() != null);
    }

    public void testGetListing() throws Exception
    {
        /*
        String[] result = ThreadUtil.getListing();
        String result2 = StringUtil.cat(result, NEWLINE);
        */
    }
}
