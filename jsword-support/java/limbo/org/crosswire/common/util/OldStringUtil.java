package org.crosswire.common.util;

import org.apache.commons.lang.StringUtils;

/**
 * .
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
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class OldStringUtil
{

    /**
     * Ensure a string is of a fixed length by truncating it or
     * by adding spaces until it is.
     * @param str The string to check
     * @param len The number of characters needed
     */
    public static String setLengthRightPad(String str, int len)
    {
        int diff = len - str.length();

        if (diff == 0)
        {
            return str;
        }

        if (diff < 0)
        {
            return str.substring(0, len);
        }
        else
        {
            return StringUtils.rightPad(str, len);
        }
    }

    /**
     * Ensure a string is of a fixed length by truncating it or
     * by adding spaces until it is.
     * @param str The string to check
     * @param len The number of characters needed
     */
    public static String setLengthLeftPad(String str, int len)
    {
        int diff = len - str.length();

        if (diff == 0)
        {
            return str;
        }

        if (diff < 0)
        {
            return str.substring(0, len);
        }
        else
        {
            return StringUtils.leftPad(str, len);
        }
    }

    /**
     * Like setLength() however this method only shortens strings that are too
     * long, and it shortens them in a human friendly way, currently this is
     * limited to adding "..." to show that it has been shortened, but we could
     * implement a fancy remove spaces/vowels algorythm.
     * @param str The string to check
     * @param len The number of characters needed
     */
    public static String shorten(String str, int len)
    {
        if (str.length() <= len)
        {
            return str;
        }

        return str.substring(0, len-3) + "..."; //$NON-NLS-1$
    }

    /**
     * For example getCapitals("Java DataBase Connectivity") = "JDBC" and
     * getCapitals("Church of England") = "CE".
     * A character is tested for capitalness using Character.isUpperCase
     * @param words The phrase from which to get the capital letters.
     * @return The capital letters in the given words.
     * @see #getInitials(String)
     */
    public static String getCapitals(String words)
    {
        StringBuffer retcode = new StringBuffer();

        for (int i=0; i<words.length(); i++)
        {
            char c = words.charAt(i);
            if (Character.isUpperCase(c))
                retcode.append(c);
        }

        return retcode.toString();
    }

    /**
     * This function creates a Java style name from a
     * variable name type input. For example calling:
     *   StringUtil.createTitle("one_two") = "OneTwo"
     *   StringUtil.createTitle("oneTwo") = "OneTwo"
     */
    public static String createJavaName(String variable)
    {
        StringBuffer retcode = new StringBuffer();
        boolean newword = true;

        for (int i=0; i<variable.length(); i++)
        {
            char c = variable.charAt(i);

            if (Character.isLetterOrDigit(c))
            {
                if (newword)
                {
                    retcode.append(Character.toUpperCase(c));
                }
                else
                {
                    retcode.append(c);
                }
            }

            newword = !Character.isLetter(c);
        }

        return retcode.toString();
    }
}
