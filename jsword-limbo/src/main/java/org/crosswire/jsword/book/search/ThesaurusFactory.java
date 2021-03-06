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
package org.crosswire.jsword.book.search;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.PluginUtil;

/**
 * Factory method for creating a new Thesaurus.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ThesaurusFactory {
    /**
     * Prevent instantiation
     */
    private ThesaurusFactory() {
    }

    /**
     * Create a new Thesaurus.
     */
    public static Thesaurus createThesaurus() throws InstantiationException {
        try {
            return (Thesaurus) PluginUtil.getImplementation(Thesaurus.class);
        } catch (Exception ex) {
            log.error("createThesaurus failed", ex);
            throw new InstantiationException();
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ThesaurusFactory.class);
}
