package org.crosswire.jsword.book.remote;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.basic.DefaultBookMetaData;
import org.jdom.Document;

/**
 * Unit test.
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
public class ConverterTest extends TestCase
{
    /**
     * Constructor for ConverterTest.
     * @param arg0
     */
    public ConverterTest(String arg0)
    {
        super(arg0);
    }

    public void testConvertMetaData() throws Exception
    {
        BookMetaData[] t1;
        String[] uid1;
        BookMetaData[] t2;
        Document doc;
        RemoteBookDriver rbd = new LocalRemoteBookDriver();
 
        t1 = new BookMetaData[]
        {
            new DefaultBookMetaData(rbd, null, "v1", BookType.BIBLE, BookMetaData.SPEED_MEDIUM), //$NON-NLS-1$
            new DefaultBookMetaData(rbd, null, "v2", BookType.BIBLE, BookMetaData.SPEED_MEDIUM), //$NON-NLS-1$
            new DefaultBookMetaData(rbd, null, "v3", BookType.BIBLE, BookMetaData.SPEED_MEDIUM), //$NON-NLS-1$
            new DefaultBookMetaData(rbd, null, "test version", BookType.BIBLE, BookMetaData.SPEED_MEDIUM, "tv", "PD", "http://nowhere.com/test", "1900-10-20"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            new DefaultBookMetaData(rbd, null, "test version", BookType.BIBLE, BookMetaData.SPEED_MEDIUM, null, (String) null, null, null), //$NON-NLS-1$
        };
        uid1 = new String[]
        {
            "v1", //$NON-NLS-1$
            "v2", //$NON-NLS-1$
            "v3", //$NON-NLS-1$
            "v4", //$NON-NLS-1$
            "v5", //$NON-NLS-1$
        };

        doc = Converter.convertBookMetaDatasToDocument(t1, uid1);
        t2 = Converter.convertDocumentToBookMetaDatas(rbd, doc, new FixtureRemoter(), BookMetaData.SPEED_INACCURATE);
        assertEquals(t1.length, 5);
        assertEquals(t2.length, 5);

        for (int i=0; i<t1.length; i++)
        {
            assertEquals(t1[i].getName(), t2[i].getName());
            //assertEquals(uid1[i], driver.getID(t2[i]));
            assertEquals(t1[i].getName(), t2[i].getName());
            assertEquals(t1[i].getEdition(), t2[i].getEdition());
            // We scrapped this test because exact times were getting confused
            //assertEquals(t1[i].getFirstPublished(), t2[i].getFirstPublished());
            assertEquals(t1[i].getInitials(), t2[i].getInitials());
            assertEquals(t1[i].getLicence(), t2[i].getLicence());
            assertEquals(t1[i].getOpenness(), t2[i].getOpenness());
            // this did check for not equals - surely this was wrong???
            assertTrue(t1[i].equals(t2[i]));
        }

        t1 = new BookMetaData[] { };
        doc = Converter.convertBookMetaDatasToDocument(t1, new String[] { });
        t2 = Converter.convertDocumentToBookMetaDatas(null, doc, null, BookMetaData.SPEED_INACCURATE);
        assertEquals(t1.length, 0);
        assertEquals(t2.length, 0);
    }

    public void testConvertPassage() throws Exception
    {
        /*
        Passage p1;
        Key p2;
        Document doc;

        p1 = PassageFactory.createPassage("Gen 1:1"); //$NON-NLS-1$
        doc = Converter.convertKeyListToDocument(p1);
        p2 = Converter.convertDocumentToKeyList(doc);
        assertEquals(p1, p2);

        p1 = PassageFactory.createPassage(""); //$NON-NLS-1$
        doc = Converter.convertKeyListToDocument(p1);
        p2 = Converter.convertDocumentToKeyList(doc);
        assertEquals(p1, p2);

        p1 = PassageFactory.createPassage("Gen-Rev"); //$NON-NLS-1$
        doc = Converter.convertKeyListToDocument(p1);
        p2 = Converter.convertDocumentToKeyList(doc);
        assertEquals(p1, p2);
        */
    }

    public void testConvertStartsWith() throws Exception
    {
        List l1;
        Iterator t1;
        Iterator t2;
        Document doc;

        l1 = Arrays.asList(new String[] { "v1", "v2", "v3" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        t1 = l1.iterator();
        doc = Converter.convertStartsWithToDocument(t1);
        t2 = Converter.convertDocumentToStartsWith(doc);
        assertEquals(l1.iterator(), t2);

        l1 = Arrays.asList(new String[] { });
        t1 = l1.iterator();
        doc = Converter.convertStartsWithToDocument(t1);
        t2 = Converter.convertDocumentToStartsWith(doc);
        assertEquals(l1.iterator(), t2);

        l1 = Arrays.asList(new String[] { "v", "v", "v" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        t1 = l1.iterator();
        doc = Converter.convertStartsWithToDocument(t1);
        t2 = Converter.convertDocumentToStartsWith(doc);
        assertEquals(l1.iterator(), t2);
    }
    
    public void testConvertException() throws Exception
    {
        Exception ex1;
        Exception ex2;
        Document doc;

        ex1 = new NullPointerException("message"); //$NON-NLS-1$
        doc = Converter.convertExceptionToDocument(ex1);
        ex2 = Converter.convertDocumentToException(doc);
        assertEquals(ex1.getMessage(), ex2.getMessage());
        assertEquals(ex1.getClass(), ex2.getClass());
    }

    public void assertEquals(Iterator it1, Iterator it2)
    {
        while (it1.hasNext())
        {
            Object ele1 = it1.next();
            Object ele2 = it2.next();
            assertEquals(ele1, ele2);
        }
        assertTrue(!it2.hasNext());
    }
}