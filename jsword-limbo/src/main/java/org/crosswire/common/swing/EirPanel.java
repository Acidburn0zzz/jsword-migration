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
package org.crosswire.common.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * EirPanel is an extension of JPanel that adds the ability to be visible in
 * a separate dialog.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class EirPanel extends JPanel
{
	// I18N: This class has not been internationalized, because it is not used.
    /**
     * A method to be exposed by our children
     * @param parent The component to which to attach the new dialog
     * @param title The title for the new dialog
     * @param modal
     */
    public void showInDialog(Component parent, String title, boolean modal)
    {
        if (dlgMain != null)
        {
            dlgMain.setVisible(true);
            return;
        }

        // I18N: migrate this to an ActionFactory
        btnOk = new JButton();
        btnOk.setText(Msg.CLOSE.toString());
        btnOk.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                close();
            }
        });

        pnlButtons = new JPanel();
        pnlButtons.add(btnOk, null);

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        pnlMain = new JPanel();
        pnlMain.setLayout(new BorderLayout());
        pnlMain.add(pnlButtons, BorderLayout.SOUTH);
        pnlMain.add(this, BorderLayout.CENTER);

        dlgMain = new JDialog(JOptionPane.getFrameForComponent(parent));
        dlgMain.getContentPane().add(pnlMain);
        dlgMain.setTitle(title);
        dlgMain.setModal(modal);
        dlgMain.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosed(WindowEvent ev)
            {
                close();
            }
        });
        dlgMain.pack();
        dlgMain.setVisible(true);
        dlgMain.setLocationRelativeTo(parent);
    }

    /**
     * Close this dialog
     */
    protected void close()
    {
        if (dlgMain != null)
        {
            dlgMain.dispose();
            dlgMain = null;
        }
    }

    private JDialog dlgMain;
    private JPanel pnlMain;
    private JPanel pnlButtons;
    private JButton btnOk;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3617013061509265206L;
}
