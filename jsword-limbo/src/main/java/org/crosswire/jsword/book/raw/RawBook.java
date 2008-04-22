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
package org.crosswire.jsword.book.raw;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.CaseType;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.SentenceUtil;
import org.crosswire.jsword.book.basic.AbstractPassageBook;
import org.crosswire.jsword.book.basic.DefaultBookMetaData;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.filter.FilterFactory;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.BibleInfo;
import org.jdom.Element;

/**
 * RawBook is a custom Bible. It is designed to be:<ul>
 * <li>Compact: So that the download time is as small as possible
 * <li>Divisible: So that a download can be partial, and some text
 *     can be read whilst missing content like styles, notes, or
 *     even word case.
 * </ul>
 * <p>As a result of this is can be very slow, or very memory hungry.
 * I guess that the technology developed here could be useful as a
 * delivery format, but the timings I am getting from my benchmarks
 * say "start again".</p>
 *
 * <p>There is a question mark over how this format will handle rich
 * text. The dictionary lookup scheme can be very space efficient
 * but I'm not sure how to embed strongs numbers with the same
 * efficiency.</p>
 *
 * <p>The algorithm I have implemented here is not perfect. To get a list
 * of the verses it gets 'wrong' see generate.log.
 * There are 2 reasons for problems. The RawBook does not take note of
 * double spaces. And we incorrectly capitalize hyphenated words at the
 * beginning of sentences.</p>
 *
 * <p>This is in part converted from the VB code that I wrote ages ago
 * that does asimilar job.</p>
 * <pre>
 * Public Sub WritePassage(sText As String, lPassageID As Long, bLang As Byte, lBibleID As Long)
 *
 *   Static bItalic As Boolean
 *
 *   Dim mWordInsts As Collection
 *
 *   Dim iNext As Long
 *   Dim iTemp As Long
 *   Dim iLast As Long
 *   Dim bDash As Boolean
 *   Dim sWord As String
 *   Dim bThisItalic As Boolean
 *   Dim iStart As Long
 *   Dim iEnd As Long
 *   Dim sNote As String
 *   Dim mNotes As Collection
 *   Dim vNoteStr As Variant
 *   Dim iNumNotes As Long
 *   Dim lWordInstID As Long
 *
 *   Set mWordInsts = New Collection
 *   iNext = 1
 *   iTemp = 1
 *   iLast = 1
 *   bDash = False
 *   iNumNotes = 1
 *
 *   ' For each real word in the verse
 *   Do
 *
 *     ' If this word contains a "{" then it is part of a comment
 *     ' and not a word. We need to strip out sets of comments
 *     Set mNotes = New Collection
 *     Do
 *       ' Decide how long this word is
 *       iNext = InStr(iLast, sText, " ")
 *       iTemp = InStr(iLast, sText, "--")
 *       If iTemp = iLast Then iTemp = 0
 *       If iTemp <> 0 And iTemp &lt; iNext Then
 *         iNext = iTemp
 *         bDash = True
 *       Else
 *         bDash = False
 *       End If
 *
 *       ' If this is the end add in the rest otherwise just add in this word
 *       If iNext = 0 Then
 *         sWord = Mid$(sText, iLast, Len(sText) - iLast + 1)
 *       Else
 *         sWord = Mid$(sText, iLast, iNext - iLast)
 *       End If
 *
 *
 *       ' Strip out the notes
 *       ' If this word is not a comment
 *       iStart = InStr(iLast, sText, "{")
 *       If iStart = 0 Then Exit Do
 *       If iStart > iLast Then Exit Do
 *
 *       ' Check we have a start and an end
 *       iEnd = InStr(iLast, sText, "}")
 *
 *       ' Add the note in
 *       sNote = Mid$(sText, iStart + 1, iEnd - iStart - 1)
 *       mNotes.Add sNote
 *
 *       ' Adjust where we are looking for words
 *       iLast = iEnd + 2
 *       If iLast > Len(sText) Then
 *         iNext = 0
 *         sWord = ""
 *         Exit Do
 *       End If
 *     Loop
 *
 *     ' Are there any notes to add?
 *     If mNotes.Count <> 0 Then
 *       ' If there is no previous word to add to then create one
 *       If mWordInsts.Count = 0 Then
 *         lWordInstID = WriteWordInst(lPassageID, 1, lBibleID)
 *         SetWordInstItalic lWordInstID, bItalic
 *         mWordInsts.Add lWordInstID
 *       End If
 *
 *       ' So add the notes to the previous word
 *       For Each vNoteStr In mNotes
 *         sNote = vNoteStr
 *         WriteNote mWordInsts.Item(mWordInsts.Count), iNumNotes, sNote
 *         iNumNotes = iNumNotes + 1
 *       Next
 *     End If
 *     Set mNotes = Nothing
 *
 *
 *     ' Italics
 *     ' Do we have a start italic char
 *     If InStr(sWord, "[") Then
 *       bItalic = True
 *       sWord = RemoveChar(sWord, "[")
 *     End If
 *
 *     ' Remember the state for this letter
 *     bThisItalic = bItalic
 *
 *     ' do we have an end italic char
 *     If InStr(sWord, "]") Then
 *       bItalic = False
 *       sWord = RemoveChar(sWord, "]")
 *     End If
 *
 *
 *     ' Actually add the word in
 *     If sWord <> "" Then
 *       AddWord mWordInsts, sWord, lPassageID, bLang, lBibleID, bThisItalic
 *     End If
 *
 *
 *     ' Add one an extra one to the last used only for a Space split
 *     If bDash Then
 *       iLast = iNext
 *     Else
 *       iLast = iNext + 1
 *     End If
 *
 *   Loop Until iNext = 0
 *   Set mWordInsts = Nothing
 *
 * End Sub
 * </pre>
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class RawBook extends AbstractPassageBook
{
    /**
     * Simple ctor
     */
    public RawBook(BookDriver driver, Map prop, URI uri)
    {
        super(null); // set the BookMetaData later
        setBookMetaData(new DefaultBookMetaData(driver, this, prop));

        this.uri = uri;

        boolean create = false;
        if (create)
        {
            memory = false;
        }
        else
        {
            memory = defaultMemory;
        }

        try
        {
            // Without these we can't go on
            wordItems = new WordItemsMem(this, create);

            if (memory)
            {
                wordInsts = new WordInstsMem(this, create);
            }
            else
            {
                wordInsts = new WordInstsDisk(this, create);
            }

            // We can still produce text without these though so they
            // should not except if the load fails.
            StringBuffer messages = new StringBuffer();

            if (memory)
            {
                puncInsts = new PuncInstsMem(this, create, messages);
            }
            else
            {
                puncInsts = new PuncInstsDisk(this, create, messages);
            }

            puncItems = new PuncItemsMem(this, create, messages);
            caseInsts = new CaseInstsMem(this, create, messages);
            paraInsts = new ParaInstsMem(this, create, messages);

            // So if any of them have failed to load we have a record of it.
            // We can carry on work fine, but shouldn't we be telling someone?

            /* should have this configurable? */
            //createSearchCache();
        }
        catch (IOException ex)
        {
            log.error("Failed to load indexes.", ex); //$NON-NLS-1$
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#getFilter()
     */
    protected Filter getFilter()
    {
        return FilterFactory.getDefaultFilter();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#contains(org.crosswire.jsword.passage.Key)
     */
    public boolean contains(Key key)
    {
        return getRawText(key) != null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#getRawText(org.crosswire.jsword.passage.Key)
     */
    public String getRawText(Key key)
    {
        StringBuffer retcode = new StringBuffer();
        Verse verse = KeyUtil.getVerse(key);

        int[] wordIdxs = wordInsts.getIndexes(verse);
        int[] caseIdxs = caseInsts.getIndexes(verse);
        int[] puncIdxs = puncInsts.getIndexes(verse);

        for (int j = 0; j < wordIdxs.length; j++)
        {
            String punc = null;
            String word = null;

            try
            {
                int puncIdx = puncIdxs[j];
                int wordIdx = wordIdxs[j];
                int caseIdx = caseIdxs[j];

                punc = puncItems.getItem(puncIdx);
                word = CaseType.fromInteger(caseIdx).setCase(wordItems.getItem(wordIdx));
            }
            catch (Exception ex)
            {
                Reporter.informUser(this, ex);
            }

            retcode.append(punc);
            retcode.append(word);
        }

        try
        {
            if (puncIdxs.length != 0)
            {
                retcode.append(puncItems.getItem(puncIdxs[puncIdxs.length - 1]));
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }

        return retcode.toString().trim();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.basic.AbstractPassageBook#setRawText(org.crosswire.jsword.passage.Key, java.lang.String)
     */
    public void setRawText(Key key, String rawData) throws BookException
    {
        throw new BookException(Msg.DRIVER_READONLY);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#setAliasKey(org.crosswire.jsword.passage.Key, org.crosswire.jsword.passage.Key)
     */
    public void setAliasKey(Key alias, Key source) throws BookException
    {
        throw new BookException(Msg.DRIVER_READONLY);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Index#findWord(java.lang.String)
     */
    public Key findWord(String word)
    {
        if (word == null)
        {
            return createEmptyKeyList();
        }

        int wordIdx = wordItems.getIndex(word);

        // Are we caching searches?
        if (cache != null && cache[wordIdx] != null)
        {
            return cache[wordIdx];
        }

        // Do the real seacrh
        Key key = createEmptyKeyList();
        try
        {
            int total = BibleInfo.versesInBible();

            for (int ord = 1; ord <= total; ord++)
            {
                int[] wordItemIds = wordInsts.getIndexes(ord);
                for (int i = 0; i < wordItemIds.length; i++)
                {
                    if (wordItemIds[i] == wordIdx)
                    {
                        key.addAll(new Verse(ord));
                    }
                }
            }
        }
        catch (NoSuchVerseException ex)
        {
            assert false : ex;
        }

        return key;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Index#getStartsWith(java.lang.String)
     */
    public Collection getStartsWith(String word)
    {
        return ((WordItemsMem) wordItems).getStartsWith(word);
    }

    public void setDocument(Verse verse, BookData bdata) throws BookException
    {
        // For all of the sections
        Iterator sit = bdata.getOsisFragment().getContent().iterator();
        while (sit.hasNext())
        {
            Element div = (Element) sit.next();

            // For all of the Verses in the section
            for (Iterator vit = div.getContent().iterator(); vit.hasNext();)
            {
                Object data = vit.next();
                if (data instanceof Element)
                {
                    Element overse = (Element) data;

                    String text = OSISUtil.getPlainText(overse);

                    // Is this verse part of a new paragraph? Since the move to OSIS
                    // the concept of new para is not what it was. I don't intend to
                    // fix it properly since Raw does not fit well with marked-up
                    // text.
                    paraInsts.setPara(false, verse);

                    // Chop the sentence into words.
                    String[] textArray = SentenceUtil.tokenize(text);

                    // The word index
                    String[] wordArray = SentenceUtil.stripPunctuation(textArray);
                    int[] wordIndexes = wordItems.getIndex(wordArray);
                    wordInsts.setIndexes(wordIndexes, verse);

                    // The punctuation index
                    String[] puncArray = SentenceUtil.stripWords(textArray);
                    int[] puncIndexes = puncItems.getIndex(puncArray);
                    puncInsts.setIndexes(puncIndexes, verse);

                    // The case index
                    int[] caseIndexes = getCases(wordArray);
                    caseInsts.setIndexes(caseIndexes, verse);
                }
                else
                {
                    log.error("Ignoring non OSIS/Verse content of DIV."); //$NON-NLS-1$
                }
            }
        }
    }

    /**
     * From a sentence get a list of words (in original order) without
     * any punctuation, and all in lower case.
     * @param words an array of words to find punctuation from
     * @return Array of case definitions
     */
    private int[] getCases(String[] words)
    {
        int[] retcode = new int[words.length];
    
        // Remove the punctuation from the ends of the words.
        for (int i = 0; i < words.length; i++)
        {
            retcode[i] = CaseType.getCase(words[i]).toInteger();
        }
    
        return retcode;
    }


    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.local.LocalURLBook#init(org.crosswire.jsword.book.Bible, org.crosswire.jsword.book.WorkListener)
     *
    public void generateText(Book source) throws BookException
    {
        init(true);

        super.generateText(source);
    }
    */

    /**
     * The base URL from which to read files
     */
    protected URI getURI()
    {
        return uri;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.local.LocalURLBook#flush()
     *
    public void flush() throws BookException
    {
        try
        {
            word_items.save();
            word_insts.save();

            punc_items.save();
            punc_insts.save();

            case_insts.save();
            para_insts.save();

            // generateSearchCache();
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.FLUSH_FAIL, ex);
        }

        super.flush();
    }
    */

    /**
     * Accessor for the list of Words. For testing only
     */
    protected WordItemsMem getWords()
    {
        return (WordItemsMem) wordItems;
    }

    /**
     * Accessor for the Verse/Words arrays. For testing only
     */
    protected WordInstsMem getWordData()
    {
        return (WordInstsMem) wordInsts;
    }

    /**
     * Create a cache to speed up searches.
     *
    private void createSearchCache() throws BookException
    {
        try
        {
            // Create a passage for each word
            cache = new Passage[word_items.size()];
            for (int i=0; i<word_items.size(); i++)
            {
                cache[i] = PassageFactory.createPassage();
            }

            // For each verse in the Bible
            for (int ord=1; ord<=BibleInfo.versesInBible(); ord++)
            {
                // and each word in the verse
                int[] word_items = word_insts.getIndexes(ord);
                for (int i=0; i<word_items.length; i++)
                {
                    // add the word to that words passage
                    cache[word_items[i]].add(new Verse(ord));
                }
            }
        }
        catch (NoSuchVerseException ex)
        {
            throw new BookException(Msg.FIND_FAIL, ex);
        }
    }

    /**
     * Create a cache to speed up searches.
     *
    private void deleteSearchCache() throws BookException
    {
        cache = null;
    }

    /**
     * Do the Bibles we create cache everything in memory or leave it on
     * disk and then read it at query time.
     * @return True if we are cacheing data in memory
     */
    public static boolean isDefaultCacheData()
    {
        return defaultMemory;
    }

    /**
     * Do the Bibles we create cache everything in memory or leave it on
     * disk and then read it at query time.
     * @param memory True if we are cacheing data in memory
     */
    public static void setDefaultCacheData(boolean memory)
    {
        RawBook.defaultMemory = memory;
    }

    /**
     * The URI from which we read data
     */
    private URI uri;

    /**
     * Do we instruct new RawBibles to cache data in memory?
     */
    private static boolean defaultMemory = true;

    /**
     * Constant for read-only, data in memory mode
     */
    public static final int MODE_READ_MEMORY = 0;

    /**
     * Constant for read-only, data on disk mode
     */
    public static final int MODE_READ_DISK = 1;

    /**
     * Constant for create mode
     */
    public static final int MODE_WRITE = 2;

    /**
     * The Source of Words
     */
    private Items wordItems;

    /**
     * The Source of Word Instances
     */
    private Insts wordInsts;

    /**
     * The source of Punctuation
     */
    private Items puncItems;

    /**
     * The source of Punctuation Instances
     */
    private Insts puncInsts;

    /**
     * The source of Case Instances
     */
    private Insts caseInsts;

    /**
     * The source of Para Instances
     */
    private ParaInstsMem paraInsts;

    /**
     * The cache of word searches
     */
    private Passage[] cache;

    /**
     * Are we cacheing or in on disk mode?.
     * Does this Bible cache everything in
     * memory or leave it on disk and then read it at query time.
     */
    private boolean memory = true;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(RawBook.class);
}