
package org.crosswire.jsword.map.model;

/**
 * A Rule has the ability to specify where it would like a node to be
 * positioned in space. 
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public interface Rule
{
    /**
     * Specify where it would like a node to be positioned in space.
     * Rules return an array of positions where the average of them
     * specifies the real desired position. So to specify a single place
     * simply return an array of one position. The positions are added
     * to the results from all Rules so to specify a single position
     * more strongly, return an array conataining that position many
     * times.
     * <br />
     * I expect that any Rule will not return more than 30 positions.
     * This expectation may be useful in colouring how many times to
     * include your Position(s) in the array.
     * @param map The Map to select a node from
     * @param book The book number
     * @param chapter The chapter 
     * @return An array of desired positions.
     */
    public Position[] getDesiredPosition(Map map, int book, int chapter);

    /**
     * Each call to getDesiredPosition() returns an array of Positions,
     * this method sets the preferred length of that returned array.
     * It is only a preferred length, so non-perfect Rules are free to
     * miss by one or 2.
     * @param scale The preferred length of the desired position array
     * @see #getDesiredPosition(Map, int)
     */
    public void setScale(int scale);

    /**
     * Each call to getDesiredPosition() returns an array of Positions,
     * this method gets the preferred length of that returned array.
     * @return The preferred length of the desired position array
     * @see #getDesiredPosition(Map, int)
     */
    public int getScale();
}
