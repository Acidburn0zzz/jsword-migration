
package org.crosswire.bible.view.swing.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.crosswire.common.swing.EirPanel;
import org.crosswire.common.util.Reporter;

/**
 * Display a simple test web browser in a pane
 *
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker
 */
public class WebBrowserPane extends EirPanel
{
    /**
     * Basic Constructor
     */
    public WebBrowserPane()
    {
        jbInit();
    }

    /**
     * Create the GUI
     */
    private void jbInit()
    {
        txt_url.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev) { go(); }
        });
        btn_go.setText("GO");
        btn_go.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev) { go(); }
        });
        pnl_url.setLayout(new BorderLayout(5, 5));
        pnl_url.add(txt_url, BorderLayout.CENTER);
        pnl_url.add(btn_go, BorderLayout.LINE_END);

        txt_browser.setPreferredSize(new Dimension(300, 200));
        txt_browser.setEditable(false);
        scr_browser.getViewport().add(txt_browser, null);

        this.setLayout(new BorderLayout(5, 5));
        this.add(pnl_url, BorderLayout.NORTH);
        this.add(scr_browser, BorderLayout.CENTER);
    }

    /**
     * Show this Panel in a new dialog
     */
    public void showInDialog(Component parent)
    {
        showInDialog(parent, "Web Browser", false);
    }

    /**
     * When someone wants to view a new page
     */
    private void go()
    {
        try
        {
            String url = txt_url.getText();
            txt_browser.setPage(url);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /* GUI Components */
    private JScrollPane scr_browser = new JScrollPane();
    private JPanel pnl_url = new JPanel();
    private JEditorPane txt_browser = new JEditorPane();
    private JTextField txt_url = new JTextField();
    private BorderLayout borderLayout2 = new BorderLayout();
    private JButton btn_go = new JButton();
}
