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
package org.crosswire.common.config.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.crosswire.common.config.Choice;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.GuiConvert;

/**
 * A color selection.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ColorField extends JPanel implements Field {
    /**
     * Create a new FileField
     */
    public ColorField() {
        actions = new ActionFactory(this);

        // FIXME(DMS)
        JButton edit = new JButton(actions.addAction(EDIT));
        edit.setIcon(new CustomIcon());
        edit.setMargin(new Insets(1, 2, 1, 1));

        setLayout(new BorderLayout());
        add(edit, BorderLayout.LINE_START);
        // add(text, BorderLayout.LINE_END);
    }

    /**
     * Do the edit action
     */
    public void doEditColor() {
        color = JColorChooser.showDialog(ColorField.this, LimboMsg.EDIT.toString(), color);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.config.swing.Field#setChoice(org.crosswire.common
     * .config.Choice)
     */
    public void setChoice(Choice param) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.swing.Field#getValue()
     */
    public String getValue() {
        return GuiConvert.color2String(color);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.swing.Field#setValue(java.lang.String)
     */
    public void setValue(String value) {
        color = GuiConvert.string2Color(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.swing.Field#getComponent()
     */
    public JComponent getComponent() {
        return this;
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        actions = new ActionFactory(this);
        is.defaultReadObject();
    }

    private static final String EDIT = "EditColor";

    /**
     * The action factory for the buttons
     */
    private transient ActionFactory actions;

    /**
     * The current Color
     */
    protected Color color = Color.white;

    /**
     * The icon square size
     */
    private static final int SIZE = 16;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3617291237934053686L;

    /**
     * The CustomIcon that shows the selected color
     */
    class CustomIcon implements Icon {
        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.Icon#getIconHeight()
         */
        public int getIconHeight() {
            return SIZE;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.Icon#getIconWidth()
         */
        public int getIconWidth() {
            return SIZE;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.Icon#paintIcon(java.awt.Component,
         * java.awt.Graphics, int, int)
         */
        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (color == null) {
                g.setColor(Color.black);
                g.drawRect(x, y, SIZE, SIZE);
                g.drawLine(x, y, x + SIZE, y + SIZE);
                g.drawLine(x + SIZE, y, x, y + SIZE);
            } else {
                g.setColor(color);
                g.fillRect(x, y, SIZE, SIZE);
                g.setColor(Color.black);
                g.drawRect(x, y, SIZE, SIZE);
            }
        }
    }
}
