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
package org.crosswire.jsword.book.search;

import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.Logger;

/**
 * A Factory class for SearchSyntax.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class SearchSyntaxFactory
{
    /**
     * Prevent Instansiation
     */
    private SearchSyntaxFactory()
    {
    }

    /**
     * Create a new SearchSyntax.
     */
    public static SearchSyntax getSearchSyntax()
    {
        return instance;
    }

    /**
     * The singleton
     */
    private static SearchSyntax instance;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SearchSyntaxFactory.class);

    /**
     * Setup the instance
     */
    static
    {
        try
        {
            Class impl = ClassUtil.getImplementor(SearchSyntax.class);
            instance = (SearchSyntax) impl.newInstance();
        }
        catch (Exception ex)
        {
            log.error("createIndexManager failed", ex); //$NON-NLS-1$
        }
    }
}
