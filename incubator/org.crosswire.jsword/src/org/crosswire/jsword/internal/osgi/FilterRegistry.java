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
 * Copyright: 2006
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.internal.osgi;

import java.util.Hashtable;

import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.filter.gbf.GBFFilter;
import org.crosswire.jsword.book.filter.osis.OSISFilter;
import org.crosswire.jsword.book.filter.plaintext.PlainTextFilter;
import org.crosswire.jsword.book.filter.thml.THMLFilter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

/**
 * This class provides Filter-related data to the classes
 * within this bundle. 
 * 
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class FilterRegistry
{

    /**
     * @param context
     */
    static void register(BundleContext context)
    {
        String filterClassName = Filter.class.getName();
        //don't reuse the same PlainText service object for the two ids, 
        //we want them to be managed separately.
        context.registerService(filterClassName, new PlainTextFilter(), createFilterProperties("plaintext"));
        context.registerService(filterClassName, new PlainTextFilter(), createFilterProperties("default"));
        context.registerService(filterClassName, new GBFFilter(), createFilterProperties("gbf"));
        context.registerService(filterClassName, new OSISFilter(), createFilterProperties("osis"));
        context.registerService(filterClassName, new THMLFilter(), createFilterProperties("thml"));

    }

    static void unregister(BundleContext context)
    {
        //Nothing to purge.
    }

    private static Hashtable createFilterProperties(String filterId)
    {
        Hashtable properties = new Hashtable();
        properties.put("filter.id", filterId);
        return properties;
    }

    /**
     * This method returns the filter for the given id. The default filter
     * is returned if the filter id has no corresponding filter 
     * (for example, if the id is misspelled or the filter has not yet 
     * been registered). If no default filter exists in this case, then
     * <code>null</code> is returned.
     * @return Returns the filter with the given filter id, if available. 
     * If the filter cannot be found, the default filter is used. If no
     * default filter exists, <code>null</code> is returned.
     */
    public static Filter getFilterById(String filterId)
    {
        BundleContext context = Activator.currentContext;
        if (context == null)
        {
            return null;
        }

        org.osgi.framework.Filter osgiFilter;
        try
        {
            osgiFilter = FrameworkUtil.createFilter("(&(objectclass=" + Filter.class.getName() + ")(filter.id=" + filterId + "))");
        }
        catch (InvalidSyntaxException e)
        {
            //This will never occur... but in case it does, bail out to the highest level.
            throw new RuntimeException("Unexpected syntax exception", e);
        }

        return (Filter) ServiceUtil.runOperation(context, osgiFilter, new FindFilterOperation());
    }

    /**
     * This method returns the default filter, if available. Otherwise it returns
     * <code>null</code>.
     * @return Returns the default filter, if available. <code>null</code> otherwise.
     */
    public static Filter getDefaultFilter()
    {
        return getFilterById("default");
    }

    private static final class FindFilterOperation implements ServiceOperation
    {
        public Object run(OperationContext context) throws Exception
        {
            return context.getService();
        }
    }
}
