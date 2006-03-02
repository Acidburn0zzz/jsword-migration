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

import java.io.Serializable;

/**
 * Some constants so that everyone can agree on the names for various methods.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ParamName implements Serializable
{
	static final ParamName PARAM_BIBLE = new ParamName("bible"); //$NON-NLS-1$
    static final ParamName PARAM_PASSAGE = new ParamName("passage"); //$NON-NLS-1$
    static final ParamName PARAM_FINDSTRING = new ParamName("word"); //$NON-NLS-1$

    /**
     * Only we should be doing this
     */
    private ParamName(String name)
    {
        this.name = name;
    }

    /**
     * Lookup method to convert from a String
     */
    public static ParamName fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            ParamName o = VALUES[i];
            if (o.name.equalsIgnoreCase(name))
            {
                return o;
            }
        }
        // cannot get here
        assert false;
        return null;
    }

    /**
     * Lookup method to convert from an integer
     */
    public static ParamName fromInteger(int i)
    {
        return VALUES[i];
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object o)
    {
        return super.equals(o);
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode()
    {
        return super.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return name;
    }

    /**
     * The name of the ParamName
     */
    private String name;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final ParamName[] VALUES =
    {
        PARAM_BIBLE,
        PARAM_PASSAGE,
        PARAM_FINDSTRING,
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257290244624626230L;
}
