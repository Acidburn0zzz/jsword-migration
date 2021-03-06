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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.jdom.Document;

/**
 * Unit test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ConverterTest extends TestCase {
    /**
     * Constructor for ConverterTest.
     * 
     * @param arg0
     */
    public ConverterTest(String arg0) {
        super(arg0);
    }

    public void testConvertMetaData() {
        /*
         * 
         * BookMetaData[] t1; String[] uid1; BookMetaData[] t2; Document doc;
         * RemoteBookDriver rbd = new LocalRemoteBookDriver();
         * 
         * t1 = new BookMetaData[] { new DefaultBookMetaData(rbd, null, "v1",
         * BookCategory.BIBLE), new DefaultBookMetaData(rbd, null,
         * "v2", BookCategory.BIBLE), new DefaultBookMetaData(rbd,
         * null, "v3", BookCategory.BIBLE), new
         * DefaultBookMetaData(rbd, null, "test version", BookCategory.BIBLE),
         * new DefaultBookMetaData(rbd, null, "test version",
         * BookCategory.BIBLE), }; uid1 = new String[] { "v1",
         * "v2", "v3", "v4",
         * "v5", };
         * 
         * doc = Converter.convertBookMetaDatasToDocument(t1, uid1); t2 =
         * Converter.convertDocumentToBookMetaDatas(rbd, doc, new
         * FixtureRemoter()); assertEquals(t1.length, 5);
         * assertEquals(t2.length, 5);
         * 
         * for (int i=0; i<t1.length; i++) { assertEquals(t1[i].getName(),
         * t2[i].getName()); //assertEquals(uid1[i], driver.getID(t2[i]));
         * assertEquals(t1[i].getName(), t2[i].getName()); // We scrapped this
         * test because exact times were getting confused
         * assertEquals(t1[i].getInitials(), t2[i].getInitials()); // this did
         * check for not equals - surely this was wrong???
         * assertTrue(t1[i].equals(t2[i])); }
         * 
         * t1 = new BookMetaData[] { }; doc =
         * Converter.convertBookMetaDatasToDocument(t1, new String[] { }); t2 =
         * Converter.convertDocumentToBookMetaDatas(null, doc, null);
         * assertEquals(t1.length, 0); assertEquals(t2.length, 0);
         */
    }

    public void testConvertPassage() {
        /*
         * Passage p1; Key p2; Document doc;
         * 
         * p1 = PassageFactory.createPassage("Gen 1:1"); doc =
         * Converter.convertKeyListToDocument(p1); p2 =
         * Converter.convertDocumentToKeyList(doc); assertEquals(p1, p2);
         * 
         * p1 = PassageFactory.createPassage(""); doc =
         * Converter.convertKeyListToDocument(p1); p2 =
         * Converter.convertDocumentToKeyList(doc); assertEquals(p1, p2);
         * 
         * p1 = PassageFactory.createPassage("Gen-Rev"); doc =
         * Converter.convertKeyListToDocument(p1); p2 =
         * Converter.convertDocumentToKeyList(doc); assertEquals(p1, p2);
         */
    }

    public void testConvertStartsWith() {
        List l1;
        Iterator t1;
        Iterator t2;
        Document doc;

        l1 = Arrays.asList(new String[] {
                "v1", "v2", "v3"});
        t1 = l1.iterator();
        doc = Converter.convertStartsWithToDocument(t1);
        t2 = Converter.convertDocumentToStartsWith(doc);
        assertEquals(l1.iterator(), t2);

        l1 = Arrays.asList(new String[] {});
        t1 = l1.iterator();
        doc = Converter.convertStartsWithToDocument(t1);
        t2 = Converter.convertDocumentToStartsWith(doc);
        assertEquals(l1.iterator(), t2);

        l1 = Arrays.asList(new String[] {
                "v", "v", "v"});
        t1 = l1.iterator();
        doc = Converter.convertStartsWithToDocument(t1);
        t2 = Converter.convertDocumentToStartsWith(doc);
        assertEquals(l1.iterator(), t2);
    }

    public void testConvertException() throws Exception {
        Exception ex1;
        Exception ex2;
        Document doc;

        ex1 = new NullPointerException("message");
        doc = Converter.convertExceptionToDocument(ex1);
        ex2 = Converter.convertDocumentToException(doc);
        assertEquals(ex1.getMessage(), ex2.getMessage());
        assertEquals(ex1.getClass(), ex2.getClass());
    }

    public void assertEquals(Iterator it1, Iterator it2) {
        while (it1.hasNext()) {
            Object ele1 = it1.next();
            Object ele2 = it2.next();
            assertEquals(ele1, ele2);
        }
        assertTrue(!it2.hasNext());
    }
}
