package org.crosswire.common.util;

/**
 * .
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class OldStringUtil
{
    /**
     * For example getCapitals("Java DataBase Connectivity") = "JDBC" and
     * getCapitals("Church of England") = "CE".
     * A character is tested for capitalness using Character.isUpperCase
     * @param words The phrase from which to get the capital letters.
     * @return The capital letters in the given words.
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