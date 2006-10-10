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
 * ID: $Id: Msg.java 763 2005-07-27 19:26:43 -0400 (Wed, 27 Jul 2005) dmsmith $
 */
package org.crosswire.common.config.swing;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class Msg extends MsgBase
{
    static final Msg EDIT = new Msg("ColorField.Edit"); //$NON-NLS-1$
    static final Msg CONFIG_SAVE_FAILED = new Msg("ConfigEditorFactory.ConfigFileFailed"); //$NON-NLS-1$
    static final Msg SELECT_FONT = new Msg("FontField.SelectFont"); //$NON-NLS-1$
    static final Msg CLASS = new Msg("MapField.Class"); //$NON-NLS-1$
    static final Msg NAME = new Msg("MapField.Name"); //$NON-NLS-1$
    static final Msg COMPONENT_EDITOR = new Msg("MapField.ComponentEditor"); //$NON-NLS-1$
    static final Msg EDIT_CLASS = new Msg("MapField.EditClass"); //$NON-NLS-1$
    static final Msg CLASS_NOT_FOUND = new Msg("MapField.ClassNotFound."); //$NON-NLS-1$
    static final Msg BAD_SUPERCLASS = new Msg("MapField.BadSuperclass"); //$NON-NLS-1$
    static final Msg ERROR = new Msg("OptionsField.Error"); //$NON-NLS-1$
    static final Msg NO_OPTIONS = new Msg("OptionsField.NoOptions"); //$NON-NLS-1$
    static final Msg PATH_EDITOR = new Msg("PathField.PathEditor"); //$NON-NLS-1$
    static final Msg NEW_CLASS = new Msg("StringArrayField.NewClass"); //$NON-NLS-1$
    static final Msg BASIC = new Msg("TabbedConfigEditor.Basic"); //$NON-NLS-1$
    static final Msg SELECT_SUBNODE = new Msg("TreeConfigEditor.SelectSubnode"); //$NON-NLS-1$
    static final Msg PROPERTIES = new Msg("WizardConfigEditor.Properties"); //$NON-NLS-1$
    static final Msg PROPERTIES_POSN = new Msg("WizardConfigEditor.PropertiesPosn"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
