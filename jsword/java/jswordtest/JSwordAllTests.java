
// package default;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * JUnit Test.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class JSwordAllTests extends TestCase
{
    public JSwordAllTests(String s)
    {
        super(s);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite();

        suite.addTestSuite(org.crosswire.jsword.util.StyleTest.class);

        suite.addTestSuite(org.crosswire.jsword.passage.BibleInfoTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageConstantsTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageMixTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageSizeTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageSpeedTest.class);
        // commented out because it causes OutOfMemoryErrors.
        //suite.addTestSuite(org.crosswire.jsword.passage.TestPassageSpeedOpt.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageTallyTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageTally2Test.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageUtilTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.PassageWriteSpeedTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.VerseTest.class);
        suite.addTestSuite(org.crosswire.jsword.passage.VerseRangeTest.class);

        suite.addTestSuite(org.crosswire.jsword.book.BiblesTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.BookMetaDataTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.BookUtilTest.class);
        // commented out because the tests were very poor.
        //suite.addTestSuite(org.crosswire.jsword.book.data.jaxb.TestOsis.class);
        suite.addTestSuite(org.crosswire.jsword.book.jdbc.JDBCBibleTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.jdbc.JDBCBibleDriverTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.jdbc.JDBCBibleUtilTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.raw.RawBibleTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.raw.RawBibleDriverTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.LocalRemoteBibleTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.LocalRemoteBibleDriverTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.ser.SerBibleTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.ser.SerBibleDriverTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.stub.StubBibleTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.stub.StubBibleDriverTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.sword.SwordBibleTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.sword.SwordBibleDriverTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.ConverterTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.LocalRemoterTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.remote.RemoteMethodTest.class);

        suite.addTestSuite(org.crosswire.jsword.book.search.parse.DictionaryTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.search.parse.CustomTokenizerTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.search.parse.ParserTest.class);
        suite.addTestSuite(org.crosswire.jsword.book.search.parse.SearchWordsTest.class);

        return suite;
    }
}
