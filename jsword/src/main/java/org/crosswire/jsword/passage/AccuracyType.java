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
package org.crosswire.jsword.passage;


/**
 * Types of Accuracy for verse references.
 * For example:
 * <ul>
 * <li>Gen == BOOK_ONLY;
 * <li>Gen 1 == BOOK_CHAPTER;
 * <li>Gen 1:1 == BOOK_VERSE;
 * <li>Jude 1 == BOOK_VERSE;
 * <li>Jude 1:1 == BOOK_VERSE;
 * <li>1:1 == CHAPTER_VERSE;
 * <li>10 == BOOK_ONLY, CHAPTER_ONLY, or VERSE_ONLY
 * <ul>
 *
 * With the last one, you will note that there is a choice. By itself there is not
 * enough information to determine which one it is. There has to be a context in
 * which it is used.
 * <p>
 * It may be found in a verse range like: Gen 1:2 - 10. In this case the context of 10
 * is Gen 1:2, which is BOOK_VERSE. So in this case, 10 is VERSE_ONLY.
 * <p>
 * If it is at the beginning of a range like 10 - 22:3, it has to have more context.
 * If the context is a prior entry like Gen 2:5, 10 - 22:3, then its context is Gen 2:5,
 * which is BOOK_VERSE and 10 is VERSE_ONLY.
 * <p>
 * However if it is  Gen 2, 10 - 22:3 then the context is Gen 2, BOOK_CHAPTER so 10 is
 * understood as BOOK_CHAPTER.
 * <p>
 * As a special case, if the preceeding range is an entire chapter or book then 10 would
 * understood as CHAPTER_ONLY or BOOK_ONLY (respectively)
 * <p>
 * If the number has no preceeding context, then it is understood as being BOOK_ONLY.
 * <p>
 * In all of these examples, the start verse was being interpreted. In the case of a
 * verse that is the end of a range, it is interpreted in the context of the range's
 * start.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum AccuracyType
{
    /** The verse was specified as book, chapter and verse. For example, Gen 1:1, Jude 3 (which only has one chapter) */
    BOOK_VERSE
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#isVerse()
         */
        @Override
        public boolean isVerse()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#createStartVerse(java.lang.String, org.crosswire.jsword.passage.VerseRange, java.lang.String[])
         */
        @Override
        public Verse createStartVerse(String original, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException
        {
            int book = BibleInfo.getBookNumber(parts[0]);
            int chapter = 1;
            int verse = 1;
            if (parts.length == 3)
            {
                chapter = getChapter(book, parts[1]);
                verse = getVerse(book, chapter, parts[2]);
            }
            else
            {
                // Some books only have 1 chapter
                verse = getVerse(book, chapter, parts[1]);
            }
            return new Verse(original, book, chapter, verse);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#createEndVerse(java.lang.String, org.crosswire.jsword.passage.Verse, java.lang.String[])
         */
        @Override
        public Verse createEndVerse(String endVerseDesc, Verse verseBasis, String[] endParts) throws NoSuchVerseException
        {
            // A fully specified verse is the same regardless of whether it is a start or an end to a range.
            return createStartVerse(endVerseDesc, null, endParts);
        }
    },

     /** The passage was specified to a book and chapter (no verse). For example, Gen 1 */
    BOOK_CHAPTER
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#isChapter()
         */
        @Override
        public boolean isChapter()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#createStartVerse(java.lang.String, org.crosswire.jsword.passage.VerseRange, java.lang.String[])
         */
        @Override
        public Verse createStartVerse(String original, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException
        {
            int book = BibleInfo.getBookNumber(parts[0]);
            int chapter = getChapter(book, parts[1]);
            int verse = 1;
            return new Verse(original, book, chapter, verse);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#createEndVerse(java.lang.String, org.crosswire.jsword.passage.Verse, java.lang.String[])
         */
        @Override
        public Verse createEndVerse(String endVerseDesc, Verse verseBasis, String[] endParts) throws NoSuchVerseException
        {
            // Very similar to the start verse but we want the end of the chapter
            Verse end = createStartVerse(endVerseDesc, null, endParts);
            // except that this gives us end at verse 1, and not the book end
            return end.getLastVerseInChapter();
        }
    },

    /** The passage was specified to a book only (no chapter or verse). For example, Gen */
    BOOK_ONLY
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#isBook()
         */
        @Override
        public boolean isBook()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#createStartVerse(java.lang.String, org.crosswire.jsword.passage.VerseRange, java.lang.String[])
         */
        @Override
        public Verse createStartVerse(String original, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException
        {
            int book = BibleInfo.getBookNumber(parts[0]);
            return new Verse(original, book, 1, 1);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#createEndVerse(java.lang.String, org.crosswire.jsword.passage.Verse, java.lang.String[])
         */
        @Override
        public Verse createEndVerse(String endVerseDesc, Verse verseBasis, String[] endParts) throws NoSuchVerseException
        {
               // And we end with a book, so we need to encompass the lot
            // For example "Gen 3-Exo"
            // Very similar to the start verse but we want the end of the book
            Verse end = createStartVerse(endVerseDesc, null, endParts);
            // except that this gives us end at 1:1, and not the book end
            return end.getLastVerseInBook();
        }
    },

    /**
     * The passage was specified to a chapter and verse (no book). For example, 1:1
     */
    CHAPTER_VERSE
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#isVerse()
         */
        @Override
        public boolean isVerse()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#createStartVerse(java.lang.String, org.crosswire.jsword.passage.VerseRange, java.lang.String[])
         */
        @Override
        public Verse createStartVerse(String original, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException
        {
            int book = verseRangeBasis.getEnd().getBook();
            int chapter = getChapter(book, parts[0]);
            int verse = getVerse(book, chapter, parts[1]);

            return new Verse(original, book, chapter, verse);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#createEndVerse(java.lang.String, org.crosswire.jsword.passage.Verse, java.lang.String[])
         */
        @Override
        public Verse createEndVerse(String endVerseDesc, Verse verseBasis, String[] endParts) throws NoSuchVerseException
        {
            // Very similar to the start verse but use the verse as a basis
            int book = verseBasis.getBook();
            int chapter = getChapter(book, endParts[0]);
            int verse = getVerse(book, chapter, endParts[1]);
            return new Verse(endVerseDesc, book, chapter, verse);
        }
    },

    /** There was only a chapter number */
    CHAPTER_ONLY
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#isChapter()
         */
        @Override
        public boolean isChapter()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#createStartVerse(java.lang.String, org.crosswire.jsword.passage.VerseRange, java.lang.String[])
         */
        @Override
        public Verse createStartVerse(String original, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException
        {
            int book = verseRangeBasis.getEnd().getBook();
            int chapter = getChapter(book, parts[0]);
            return new Verse(original, book, chapter, 1);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#createEndVerse(java.lang.String, org.crosswire.jsword.passage.Verse, java.lang.String[])
         */
        @Override
        public Verse createEndVerse(String endVerseDesc, Verse verseBasis, String[] endParts) throws NoSuchVerseException
        {
            // Very similar to the start verse but use the verse as a basis
            // and it gets the end of the chapter
            int book = verseBasis.getBook();
            int chapter = getChapter(book, endParts[0]);
            Verse end = new Verse(endVerseDesc, book, chapter, 1);
            return end.getLastVerseInChapter();
        }
    },

    /** There was only a verse number */
    VERSE_ONLY
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#isVerse()
         */
        @Override
        public boolean isVerse()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#createStartVerse(java.lang.String, org.crosswire.jsword.passage.VerseRange, java.lang.String[])
         */
        @Override
        public Verse createStartVerse(String original, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException
        {
            int book = verseRangeBasis.getEnd().getBook();
            int chapter = verseRangeBasis.getEnd().getChapter();
            int verse = getVerse(book, chapter, parts[0]);
            return new Verse(original, book, chapter, verse);
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.AccuracyType#createEndVerse(java.lang.String, org.crosswire.jsword.passage.Verse, java.lang.String[])
         */
        @Override
        public Verse createEndVerse(String endVerseDesc, Verse verseBasis, String[] endParts) throws NoSuchVerseException
        {
            // Very similar to the start verse but use the verse as a basis
            // and it gets the end of the chapter
            int book = verseBasis.getBook();
            int chapter = verseBasis.getChapter();
            int verse = getVerse(book, chapter, endParts[0]);
            return new Verse(endVerseDesc, book, chapter, verse);
        }
    };

    /**
     * @param original the original verse reference as a string
     * @param verseRangeBasis the range that stood before the string reference
     * @param parts a tokenized version of the original
     * @return a <code>Verse</code> for the original
     * @throws NoSuchVerseException
     */
    public abstract Verse createStartVerse(String original, VerseRange verseRangeBasis, String[] parts) throws NoSuchVerseException;

    /**
     * @param endVerseDesc the original verse reference as a string
     * @param verseBasis the verse at the beginning of the range
     * @param endParts a tokenized version of the original
     * @return a <code>Verse</code> for the original
     * @throws NoSuchVerseException
     */
    public abstract Verse createEndVerse(String endVerseDesc, Verse verseBasis, String[] endParts) throws NoSuchVerseException;

    /**
     * @return true if this AccuracyType specifies down to the book but not chapter or verse.
     */
    public boolean isBook()
    {
        return false;
    }

    /**
     * @return true if this AccuracyType specifies down to the chapter but not the verse.
     */
    public boolean isChapter()
    {
        return false;
    }

    /**
     * @return true if this AccuracyType specifies down to the verse.
     */
    public boolean isVerse()
    {
        return false;
    }

    /**
     * Interprets the chapter value, which is either a number or "ff" or "$" (meaning "what follows")
     * @param lbook the integer representation of the book
     * @param chapter a string representation of the chapter. May be "ff" or "$" for "what follows".
     * @return the number of the chapter
     * @throws NoSuchVerseException
     */
    public static final int getChapter(int lbook, String chapter) throws NoSuchVerseException
    {
        if (isEndMarker(chapter))
        {
            return BibleInfo.chaptersInBook(lbook);
        }
        return parseInt(chapter);
    }

    /**
     * Interprets the verse value, which is either a number or "ff" or "$" (meaning "what follows")
     * @param lbook the integer representation of the book
     * @param lchapter the integer representation of the chapter
     * @param verse the string representation of the verse
     * @return the number of the verse
     * @throws NoSuchVerseException
     */
    public static final int getVerse(int lbook, int lchapter, String verse) throws NoSuchVerseException
    {
        if (isEndMarker(verse))
        {
            return BibleInfo.versesInChapter(lbook, lchapter);
        }
        return parseInt(verse);
    }

    /**
     * Determine how closely the string defines a verse.
     * @param original
     * @param parts is a reference split into parts
     * @return what is the kind of accuracy of the string reference.
     * @throws NoSuchVerseException
     */
    public static AccuracyType fromText(String original, String[] parts) throws NoSuchVerseException
    {
        return fromText(original, parts, null, null);
    }

    /**
     * @param original
     * @param parts
     * @param verseAccuracy
     * @return the accuracy of the parts
     * @throws NoSuchVerseException
     */
    public static AccuracyType fromText(String original, String[] parts, AccuracyType verseAccuracy) throws NoSuchVerseException
    {
        return fromText(original, parts, verseAccuracy, null);
    }

    /**
     * @param original
     * @param parts
     * @param basis
     * @return the accuracy of the parts
     * @throws NoSuchVerseException
     */
    public static AccuracyType fromText(String original, String[] parts, VerseRange basis) throws NoSuchVerseException
    {
        return fromText(original, parts, null, basis);
    }

    /**
     * Does this string exactly define a Verse. For example:<ul>
     * <li>fromText("Gen") == AccuracyType.BOOK_ONLY;
     * <li>fromText("Gen 1:1") == AccuracyType.BOOK_VERSE;
     * <li>fromText("Gen 1") == AccuracyType.BOOK_CHAPTER;
     * <li>fromText("Jude 1") == AccuracyType.BOOK_VERSE;
     * <li>fromText("Jude 1:1") == AccuracyType.BOOK_VERSE;
     * <li>fromText("1:1") == AccuracyType.CHAPTER_VERSE;
     * <li>fromText("1") == AccuracyType.VERSE_ONLY;
     * <ul>
     * @param parts
     * @param verseAccuracy
     * @param basis
     * @return the accuracy of the parts
     * @throws NoSuchVerseException
     */
    public static AccuracyType fromText(String original, String[] parts, AccuracyType verseAccuracy, VerseRange basis) throws NoSuchVerseException
    {
        String msg = original; //$NON-NLS-1$
        switch (parts.length)
        {
        case 1:
            if (BibleInfo.isBookName(parts[0]))
            {
                return BOOK_ONLY;
            }

            // At this point we should have a number.
            // But double check it
            checkValidChapterOrVerse(parts[0]);

            // What it is depends upon what stands before it.
            if (verseAccuracy != null)
            {
                if (verseAccuracy.isVerse())
                {
                    return VERSE_ONLY;
                }

                if (verseAccuracy.isChapter())
                {
                    return CHAPTER_ONLY;
                }
            }

            if (basis != null)
            {
                if (basis.isWholeChapter())
                {
                    return CHAPTER_ONLY;
                }
                return VERSE_ONLY;
            }

            for (int i = 0; i < parts.length; i++)
            {
                msg += ", "; //$NON-NLS-1$
                msg += parts[1];
            }

            throw new NoSuchVerseException(Msg.VERSE_PARTS, new Object[] { msg }); // AccuracyType.VERSE_ALLOWED_DELIMS });

        case 2:
            // Does it start with a book?
            int pbook = BibleInfo.getBookNumber(parts[0]);
            if (pbook == -1)
            {
                checkValidChapterOrVerse(parts[0]);
                checkValidChapterOrVerse(parts[1]);
                return CHAPTER_VERSE;
            }

            if (BibleInfo.chaptersInBook(pbook) == 1)
            {
                return BOOK_VERSE;
            }

            return BOOK_CHAPTER;

        case 3:
            if (BibleInfo.getBookNumber(parts[0]) != -1)
            {
                checkValidChapterOrVerse(parts[1]);
                checkValidChapterOrVerse(parts[2]);
                return BOOK_VERSE;
            }

            for (int i = 0; i < parts.length; i++)
            {
                msg += ", "; //$NON-NLS-1$
                msg += parts[1];
            }

            throw new NoSuchVerseException(Msg.VERSE_PARTS, new Object[] { msg }); // AccuracyType.VERSE_ALLOWED_DELIMS });

        default:
            for (int i = 0; i < parts.length; i++)
            {
                msg += ", "; //$NON-NLS-1$
                msg += parts[1];
            }

            throw new NoSuchVerseException(Msg.VERSE_PARTS, new Object[] { msg }); // AccuracyType.VERSE_ALLOWED_DELIMS });
        }
    }

    /**
     * Is this text valid in a chapter/verse context
     * @param text The string to test for validity
     * @throws NoSuchVerseException If the text is invalid
     */
    private static void checkValidChapterOrVerse(String text) throws NoSuchVerseException
    {
        if (!isEndMarker(text))
        {
            parseInt(text);
        }
    }

    /**
     * This is simply a convenience function to wrap Integer.parseInt()
     * and give us a reasonable exception on failure. It is called by
     * VerseRange hence protected, however I would prefer private
     * @param text The string to be parsed
     * @return The correctly parsed chapter or verse
     * @exception NoSuchVerseException If the reference is illegal
     */
    private static int parseInt(String text) throws NoSuchVerseException
    {
        try
        {
            return Integer.parseInt(text);
        }
        catch (NumberFormatException ex)
        {
            throw new NoSuchVerseException(Msg.VERSE_PARSE, new Object[] { text });
        }
    }

    /**
     * Is this string a legal marker for 'to the end of the chapter'
     * @param text The string to be checked
     * @return true if this is a legal marker
     */
    private static boolean isEndMarker(String text)
    {
        if (text.equals(AccuracyType.VERSE_END_MARK1))
        {
            return true;
        }

        if (text.equals(AccuracyType.VERSE_END_MARK2))
        {
            return true;
        }

        return false;
    }

    /**
     * Take a string representation of a verse and parse it into
     * an Array of Strings where each part is likely to be a verse part.
     * The goal is to allow the greatest possible variations in user input.
     * <p>Parts can be separated by pretty much anything. No distinction
     * is made between them. While chapter and verse need to be separated,
     * a separator is assumed between digits and non-digits. Adjacent words,
     * (i.e. sequences of non-digits) are understood to be a book reference.
     * If a number runs up against a book name, it is considered to be either
     * part of the book name (i.e. it is before it) or a chapter number (i.e.
     * it stands after it.)</p>
     * <p>Note: ff and $ are considered to be digits.</p>
     * <p>Note: it is not necessary for this to be a BCV (book, chapter, verse),
     * it may just be BC, B, C, V or CV. No distinction is needed here for a
     * number that stands alone.</p>
     * @param input The string to parse.
     * @return The string array
     */
    public static String[] tokenize(String input)
    {
        // The results are expected to be no more than 3 parts
        String [] args = { null, null, null, null, null, null, null, null};

        // Normalize the input by replacing non-digits and non-letters with spaces.
        int length = input.length();
        // Create an output array big enough to add ' ' where needed
        char [] normalized = new char [length * 2];
        char lastChar = '0'; // start with a digit so normalized won't start with a space
        char curChar = ' '; // can be anything
        int tokenCount = 0;
        int normalizedLength = 0;
        int startIndex = 0;
        String token = null;
        boolean foundBoundary = false;
        for (int i = 0; i < length; i++)
        {
            curChar = input.charAt(i);
            boolean charIsDigit = curChar == '$' || Character.isDigit(curChar) || (curChar == 'f' && (i + 1 < length ? input.charAt(i + 1) : ' ') == 'f');
            if (charIsDigit || Character.isLetter(curChar))
            {
                foundBoundary = true;
                boolean charWasDigit = lastChar == '$' || Character.isDigit(lastChar) || (lastChar == 'f' && (i > 2 ? input.charAt(i - 2) : '0') == 'f');
                if (charWasDigit || Character.isLetter(lastChar))
                {
                    foundBoundary = false;
                    // Replace transitions between digits and letters with spaces.
                    if (normalizedLength > 0 && charWasDigit != charIsDigit)
                    {
                        foundBoundary = true;
                    }
                }
                if (foundBoundary)
                {
                    // On a boundary:
                    // Digits always start a new token
                    // Letters always continue a previous token
                    if (charIsDigit)
                    {
                        token = new String(normalized, startIndex, normalizedLength - startIndex);
                        args[tokenCount++] = token;
                        normalizedLength = 0;
                    }
                    else
                    {
                        normalized[normalizedLength++] = ' ';
                    }
                }
                normalized[normalizedLength++] = curChar;
            }

            // Until the first character is copied, there is no last char
            if (normalizedLength > 0)
            {
                lastChar = curChar;
            }
        }
        token = new String(normalized, startIndex, normalizedLength - startIndex);
        args[tokenCount++] = token;

        String [] results = new String [tokenCount];
        System.arraycopy(args, 0, results, 0, tokenCount);
        return results;
    }

    /**
     * What characters can we use to separate parts to a verse
     */
    public static final String VERSE_ALLOWED_DELIMS = " :."; //$NON-NLS-1$

    /**
     * Characters that are used to indicate end of verse/chapter, part 1
     */
    public static final String VERSE_END_MARK1 = "$"; //$NON-NLS-1$

    /**
     * Characters that are used to indicate end of verse/chapter, part 2
     */
    public static final String VERSE_END_MARK2 = "ff"; //$NON-NLS-1$
}
