/**
 * Distribution License:
 * This is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published
 * by the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/llgpl.html
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
package org.crosswire.common.util;

import java.util.AbstractList;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * This is a version of LinkedList that is not fail-fast.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class RobustList extends AbstractList {
    /**
     * Does this list contains the specified element?
     * 
     * @param o
     *            element whose presence in this list is to be tested.
     * @return true if this list contains the specified element.
     */
    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    /**
     * Returns the number of elements in this list.
     * 
     * @return the number of elements in this list.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Appends the specified element to the end of this list.
     * 
     * @param o
     *            element to be appended to this list.
     */
    @Override
    public boolean add(Object o) {
        // debug("pre-add "+o);
        new Entry(o);
        // debug("post-add "+o);
        return true;
    }

    /**
     * Removes the element at the specified position in this list. Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     * 
     * @param index
     *            the index of the element to removed.
     * @return the element previously at the specified position.
     */
    @Override
    public Object remove(int index) {
        // debug("pre-remove "+index);
        Entry e = findEntry(index);
        e.remove();
        // debug("post-remove "+index);

        return e.object;
    }

    /**
     * Removes the first occurrence of the specified element in this list. If
     * the list does not contain the element, it is unchanged.
     * 
     * @param o
     *            element to be removed from this list, if present.
     * @return true if the list contained the specified element.
     */
    @Override
    public boolean remove(Object o) {
        // debug("pre-remove "+o);
        if (o == null) {
            Entry e = head;
            while (e != null) {
                if (e.object == null) {
                    e.remove();
                    // debug("post-remove "+o);
                    return true;
                }

                e = e.next;
            }
        } else {
            Entry e = head;
            while (e != null) {
                if (o.equals(e.object)) {
                    e.remove();
                    // debug("post-remove "+o);
                    return true;
                }

                e = e.next;
            }
        }

        // debug("post-remove fail "+o);
        return false;
    }

    /**
     * Removes all of the elements from this list.
     */
    @Override
    public void clear() {
        debug("pre-clear");
        head = null;
        foot = null;
        size = 0;
        debug("post-clear");
    }

    /**
     * Returns the element at the specified position in this list.
     * 
     * @param index
     *            index of element to return.
     * @return the element at the specified position in this list.
     */
    @Override
    public Object get(int index) {
        return findEntry(index).object;
    }

    /**
     * Return the indexed entry.
     */
    private Entry findEntry(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Entry e;
        if (index < size / 2) {
            e = head;
            for (int i = 0; i != index; i++) {
                e = e.next;
            }
        } else {
            e = foot;
            for (int i = size - 1; i != index; i--) {
                e = e.prev;
            }
        }

        return e;
    }

    /**
     * Returns the index in this list of the first occurrence of the specified
     * element, or -1 if the List does not contain this element.
     * 
     * @param o
     *            element to search for.
     * @return the index of the first occurrence or -1
     */
    @Override
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            Entry e = head;
            while (e != null) {
                if (e.object == null) {
                    return index;
                }

                e = e.next;
                index++;
            }
        } else {
            Entry e = head;
            while (e != null) {
                if (o.equals(e.object)) {
                    return index;
                }

                e = e.next;
                index++;
            }
        }

        return -1;
    }

    /**
     * Returns a list-iterator of the elements in this list
     * 
     * @return a ListIterator of the elements in this list
     */
    public Enumeration elements() {
        // debug("pre-enumerate");
        return new RobustListEnumeration(head);
    }

    /**
     * 
     */
    private static class RobustListEnumeration implements Enumeration {
        /**
         * Simple ctor
         */
        RobustListEnumeration(Entry head) {
            next = head;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Enumeration#hasMoreElements()
         */
        public boolean hasMoreElements() {
            return next != null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Enumeration#nextElement()
         */
        public Object nextElement() throws NoSuchElementException {
            if (next == null) {
                throw new NoSuchElementException();
            }

            // next.debug();
            Object retcode = next.object;
            next = next.next;

            return retcode;
        }

        private Entry next;
    }

    /**
     * 
     */
    private class Entry {
        /**
         * @param object
         */
        protected Entry(Object object) {
            this.object = object;
            this.next = null;
            this.prev = foot;

            if (head == null) {
                head = this;
            }

            if (foot != null) {
                foot.next = this;
            }

            foot = this;

            size++;
        }

        /**
         * 
         */
        protected void remove() {
            if (this == foot) {
                if (prev != null) {
                    prev.next = null;
                }

                foot = prev;
            }

            if (this == head) {
                if (next != null) {
                    next.prev = null;
                }

                head = next;
            }

            if (prev != null) {
                prev.next = next;
            }

            if (next != null) {
                next.prev = prev;
            }

            size--;
        }

        /**
         * 
         */
        protected void debug() {
            log.debug("  prev=" + prev);
            log.debug("  this=" + this);
            log.debug("  next=" + next);
            log.debug("   obje=" + object);
        }

        protected Object object;
        protected Entry next;
        protected Entry prev;
    }

    /**
     * Debug this implementation
     */
    protected void debug(String title) {
        log.debug(title);
        log.debug(" head =" + head);
        log.debug(" foot =" + foot);

        int i = 0;
        Entry e = head;
        while (e != null) {
            log.debug(" index=" + i);
            e.debug();
            e = e.next;
            i++;
        }
    }

    protected Entry head;
    protected Entry foot;
    protected int size;

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(RobustList.class);
}
