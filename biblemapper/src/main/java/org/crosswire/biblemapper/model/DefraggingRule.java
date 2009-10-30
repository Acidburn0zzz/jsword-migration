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
 * ID: $Id$
 */
package org.crosswire.biblemapper.model;

import org.crosswire.jsword.versification.BibleInfo;
import org.crosswire.common.util.Reporter;

/**
 * DefraggingRule attempts to keep all the nodes in straight lines.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class DefraggingRule extends AbstractRule {
    /**
     * Specify where it would like a node to be positioned in space. Rules
     * return an array of positions where the average of them specifies the real
     * desired position. So to specify a single place simply return an array of
     * one position. The positions are added to the results from all Rules so to
     * specify a single position more strongly, return an array conataining that
     * position many times.
     * 
     * @param map
     *            The Map to select a node from
     * @return An array of desired positions.
     */
    public Position getDesiredPosition(Map map, int book, int chapter) {
        try {
            Position reply;

            boolean at_start = (chapter == 1);
            boolean at_end = (chapter == BibleInfo.chaptersInBook(book));

            if (at_start || at_end) {
                // Where we are now
                float[] current = map.getPositionArrayCopy(book, chapter);

                // What are we trying to get away from
                /*
                 * int other_ord; if (at_start) { int end_chapter =
                 * BibleInfo.chaptersInBook(b); int end_verse =
                 * BibleInfo.versesInChapter(b, end_chapter); Verse end = new
                 * Verse(b, end_chapter, end_verse); other_ord =
                 * end.getOrdinal(); } else { other_ord = new Verse(b, 1,
                 * 1).getOrdinal(); }
                 * 
                 * float[] opposite = map.getPosition(end_ord); float[] pos =
                 * new float[];
                 * 
                 * // Um this is getting a little hard for me in n-dimensions
                 */

                reply = new Position(current);
            } else {
                Position prev = new Position(map.getPositionArrayCopy(book, chapter - 1));
                Position next = new Position(map.getPositionArrayCopy(book, chapter + 1));
                Position[] both = new Position[] {
                        prev, next
                };

                reply = PositionUtil.average(both, map.getDimensions());
            }

            return reply;
        } catch (Exception ex) {
            Reporter.informUser(this, ex);
            return null;
        }
    }
}
