package org.crosswire.bibledesktop.desktop;

import java.io.Serializable;


/**
 * Defines properties that control the behavior of translating OSIS to HTML.
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
 * @author DM Smith [ dmsmith555 at hotmail dot com]
 * @version $Id$
 */
public class XSLTProperty implements Serializable
{

    /**
     * Determines whether Strong's Numbers should show
     */
    public static final XSLTProperty STRONGS_NUMBERS = new XSLTProperty("Strongs", false); //$NON-NLS-1$

    /**
     * Determines whether verses should start on a new line.
     */
    public static final XSLTProperty START_VERSE_ON_NEWLINE = new XSLTProperty("VLine", false); //$NON-NLS-1$

    /**
     * Show verse numbers
     */
    public static final XSLTProperty VERSE_NUMBERS = new XSLTProperty("VNum", true); //$NON-NLS-1$

    /**
     * Show verse numbers as a superscript.
     */
    public static final XSLTProperty TINY_VERSE_NUMBERS = new XSLTProperty("TinyVNum", true); //$NON-NLS-1$

    /**
     * Should notes be shown
     */
    public static final XSLTProperty NOTES = new XSLTProperty("Notes", true); //$NON-NLS-1$

    /**
     * Should cross references be shown
     */
    public static final XSLTProperty XREF = new XSLTProperty("XRef", true); //$NON-NLS-1$

    /**
     * @param name The name of this property
     */
    private XSLTProperty(String name, boolean defaultState)
    {
        this.name = name;
        this.defaultState = defaultState;
        this.state = defaultState;
    }

    /**
     * @return the name of the property
     */
    public String getName()
    {
        return name;
    }

    public boolean getDefault()
    {
        return defaultState; //$NON-NLS-1$
    }

    public boolean getState()
    {
        return state;
    }

    public void setState(boolean newState)
    {
        state = newState;
    }

    /**
     * Lookup method to convert from a String
     */
    public static XSLTProperty fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            XSLTProperty o = VALUES[i];
            if (o.name.equalsIgnoreCase(name))
            {
                return o;
            }
        }
        assert false;
        return null;
    }

    /**
     * Lookup method to convert from an integer
     */
    public static XSLTProperty fromInteger(int i)
    {
        return VALUES[i];
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(Object o)
    {
        return super.equals(o);
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode()
    {
        return super.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return name;
    }

    /**
     * The name of the XSLTProperty
     */
    private String name;

    /**
     * The default state of the XSLTProperty
     */
    private boolean defaultState;

    /**
     * The current state of the XSLTProperty
     */
    private boolean state;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final XSLTProperty[] VALUES =
    {
        STRONGS_NUMBERS,
        START_VERSE_ON_NEWLINE,
        VERSE_NUMBERS,
        TINY_VERSE_NUMBERS,
        NOTES,
        XREF,
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257567325749326905L;
}