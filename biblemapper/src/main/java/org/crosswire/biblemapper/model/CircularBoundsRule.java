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

import org.crosswire.common.util.Logger;

/**
 * RectangularBoundsRule implements Rule and attempts to move the Key within the
 * space (0, 0, _) to (1, 1, _).
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class CircularBoundsRule extends AbstractRule {
    /**
     * Specify where it would like a node to be positioned in space. Rules
     * return an array of positions where the average of them specifies the real
     * desired position. So to specify a single place simply return an array of
     * one position. The positions are added to the results from all Rules so to
     * specify a single position more strongly, return an array conataining that
     * position many times. <br />
     * I expect that any Rule will not return more than 30 positions. This
     * expectation may be useful in colouring how many times to include your
     * Position(s) in the array.
     * 
     * @param map
     *            The Map to select a node from
     * @return An array of desired positions.
     */
    public Position getDesiredPosition(Map map, int book, int chapter) {
        if (map.getDimensions() != 2) {
            log.warn("CircularBoundsRule only works in 2 dimensions");
            return new Position(map.getPositionArrayCopy(book, chapter));
        }

        float[] arr = map.getPositionArrayCopy(book, chapter);

        float xcentrecoord = arr[0] - 0.5F;
        float ycentrecoord = arr[1] - 0.5F;

        float distance = (float) Math.sqrt(xcentrecoord * xcentrecoord + ycentrecoord * ycentrecoord);
        if (distance > RADIUS) {
            double angle = Math.atan(xcentrecoord / ycentrecoord);

            if (xcentrecoord > 0) {
                arr[0] = 1F - (float) (RADIUS * Math.cos(angle));
            } else {
                arr[0] = (float) (RADIUS * Math.cos(angle));
            }

            if (ycentrecoord > 0) {
                arr[1] = 1F - (float) (RADIUS * Math.sin(angle));
            } else {
                arr[1] = (float) (RADIUS * Math.sin(angle));
            }
        }

        return new Position(arr);
    }

    /** The log stream */
    private static final Logger log = Logger.getLogger(CircularBoundsRule.class);

    private static final float RADIUS = 0.45F;
}
