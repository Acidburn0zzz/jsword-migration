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
package org.crosswire.jsword.book.basic;

/**
 * LocalURLBookDriver is a helper for drivers that want to store files locally.
 * 
 * It takes care of providing you with a directory to work from and managing the
 * files stored in that directory.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class LocalURLBookDriver
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#delete()
     *
    public void delete(BookMetaData bmd) throws BookException
    {
        findBibleRoot();

        if (dir == null)
        {
            throw new BookException(Msg.DELETE_FAIL);
        }

        Book book = bmd.getBook();
        if (!(book instanceof AbstractLocalURLBook))
        {
            throw new BookException(Msg.DELETE_FAIL, new Object[] { bmd.getName()});
        }

        AbstractLocalURLBook lbook = (AbstractLocalURLBook) book;
        try
        {
            if (!NetUtil.delete(lbook.getURL()))
            {
                throw new BookException(Msg.DELETE_FAIL, new Object[] { bmd.getName() });
            }
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.DELETE_FAIL, ex, new Object[] { bmd.getName() });
        }
    }
    */

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#create(org.crosswire.jsword.book.Book, org.crosswire.jsword.book.WorkListener)
     *
    public Book create(Book book) throws BookException
    {
        findBibleRoot();

        if (dir == null)
        {
            throw new BookException(Msg.CREATE_FAIL);
        }

        if (!(book instanceof Bible))
        {
            throw new BookException(Msg.CREATE_NOBIBLE);
        }

        Bible source = (Bible) book;
        BookMetaData sbmd = source.getBookMetaData();

        try
        {
            String base = source.getName();
            URL url = createUniqueDirectory(base);

            AbstractLocalURLBook dest = (AbstractLocalURLBook) bibleclass.newInstance();

            // LATER(joe): this should not be null
            dest.init(url, null);
            dest.generateText(source);

            return dest;
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.CREATE_FAIL, ex);
        }
    }
    */

    /*
     * We need to create a unique directory in which to store the new Book data.
     * We do this by shortening the name and adding numbers until it is unique.
     * LATER(joe): there is a theoretical race condition here but this is probably ST so we are OK.
     *
    private URL createUniqueDirectory(String base) throws MalformedURLException
    {
        base = StringUtil.createJavaName(base);
        base = StringUtil.shorten(base, 10);

        URL url = NetUtil.lengthenURL(dir, base);
        if (NetUtil.isDirectory(url) || NetUtil.isFile(url))
        {
            int count = 1;
            while (true)
            {
                url = NetUtil.lengthenURL(dir, base + count);
                if (!NetUtil.isDirectory(url))
                {    
                    break;
                }

                count++;
            }
        }

        NetUtil.makeDirectory(url);
        return url;
    }
    */
}
