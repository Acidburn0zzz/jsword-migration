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

import java.util.Iterator;

/**
 * An implementation of some of the easier methods from Key.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public abstract class AbstractKeyList implements Key
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#isEmpty()
     */
    public boolean isEmpty()
    {
        return getChildCount() == 0;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#contains(org.crosswire.jsword.passage.Key)
     */
    public boolean contains(Key key)
    {
        for (Iterator it = iterator(); it.hasNext(); )
        {
            Key temp = (Key) it.next();
            if (key.equals(temp))
            {
                return true;
            }
        }

        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#retain(org.crosswire.jsword.passage.Key)
     */
    public void retainAll(Key key)
    {
        Key shared = new DefaultKeyList();
        shared.addAll(key);
        retain(this, shared);
    }

    /**
     * Utility to remove all the keys from alter that are not in base
     * @param alter The key to remove keys from
     * @param base The check key
     */
    protected static void retain(Key alter, Key base)
    {
        for (Iterator it = alter.iterator(); it.hasNext(); )
        {
            Key sublist = (Key) it.next();
            if (sublist.canHaveChildren())
            {
                retain(sublist, base);
                if (sublist.isEmpty())
                {
                    it.remove();
                }
            }
            else
            {
                if (!base.contains(sublist))
                {
                    it.remove();
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getName();
    }

    /**
     * Override the default name with a custom name.
     * If the name is null then a name will be generated by concatenating the
     * names of all the elements of this node.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getName()
     */
    public String getName()
    {
        if (name != null)
        {
            return name;
        }

        DefaultKeyVisitor visitor = new NameVisitor();
        KeyUtil.visit(this, visitor);
        return visitor.toString();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getName(org.crosswire.jsword.passage.Key)
     */
    public String getName(Key base)
    {
        return getName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOSISRef()
     */
    public String getOsisRef()
    {
        DefaultKeyVisitor visitor = new OsisRefVisitor();
        KeyUtil.visit(this, visitor);
        return visitor.toString();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.Key#getOSISId()
     */
    public String getOsisID()
    {
        DefaultKeyVisitor visitor = new OsisIDVisitor();
        KeyUtil.visit(this, visitor);
        return visitor.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj)
    {
        Key that = (Key) obj;

        Key thisfirst = (Key) this.iterator().next();
        Key thatfirst = (Key) that.iterator().next();

        if (thisfirst == null)
        {
            if (thatfirst == null)
            {
                // we are both empty, and rank the same
                return 0;
            }
            // i am empty, he is not so we are greater
            return 1;
        }

        if (thatfirst == null)
        {
            // he is empty, we are not so he is greater
            return -1;
        }

        return thisfirst.compareTo(thatfirst);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        Object clone = null;
        try
        {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            assert false : e;
        }
        return clone;
    }

    /**
     * The <code>NameVisitor</code> constructs a readable representation
     * of the Passage.
     */
    private static class NameVisitor extends DefaultKeyVisitor
    {
        /**
         * Create a <code>NameVisitor</code>.
         */
        public NameVisitor()
        {
            buffer = new StringBuffer();
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.KeyVisitor#visitLeaf(org.crosswire.jsword.passage.Key)
         */
        public void visitLeaf(Key key)
        {
            buffer.append(key.getName());
            buffer.append(AbstractPassage.REF_PREF_DELIM);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        public String toString()
        {
            String reply = buffer.toString();
            if (reply.length() > 0)
            {
                // strip off the final ", "
                reply = reply.substring(0, reply.length() - AbstractPassage.REF_PREF_DELIM.length());
            }

            return reply;
        }

        protected StringBuffer buffer;
    }

    /** 
     * The <code>OsisRefVisitor</code> constructs a readable representation
     * of the Passage, using OSIS names.
     */
    private static class OsisRefVisitor extends NameVisitor
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.KeyVisitor#visitLeaf(org.crosswire.jsword.passage.Key)
         */
        public void visitLeaf(Key key)
        {
            buffer.append(key.getOsisRef());
            buffer.append(AbstractPassage.REF_PREF_DELIM);
        }
    }

    /** 
     * The <code>OsisRefVisitor</code> constructs a readable representation
     * of the Passage, using OSIS names.
     */
    private static class OsisIDVisitor extends NameVisitor
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.KeyVisitor#visitLeaf(org.crosswire.jsword.passage.Key)
         */
        public void visitLeaf(Key key)
        {
            buffer.append(key.getOsisID());
            buffer.append(AbstractPassage.REF_OSIS_DELIM);
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        public String toString()
        {
            String reply = super.toString();
            if (reply.length() > 0)
            {
                // strip off the final " "
                reply = reply.substring(0, reply.length() - AbstractPassage.REF_OSIS_DELIM.length());
            }

            return reply;
        }
    }

    /**
     * The common user visible name for this work
     */
    private String name;
}
