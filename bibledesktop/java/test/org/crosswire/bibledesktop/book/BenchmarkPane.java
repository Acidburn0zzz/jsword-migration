
package org.crosswire.bibledesktop.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.crosswire.common.swing.EirPanel;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.test.Speed;

/**
 * BenchmarkPane allows an application to test the speed of a Bible by
 * giving it some stress tests.
 * <p>To start one of these call:
 * <pre>
 * BenchmarkPane bench = new BenchmarkPane();
 * bench.showInDialog(getComponent());
 * </pre>
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
public class BenchmarkPane extends EirPanel
{
    /**
     * Basic constructor
     */
    public BenchmarkPane()
    {
        jbInit();
    }

    /**
     * Create the GUI components
     */
    private void jbInit()
    {
        cbo_bible.setModel(mdl_bible);
        cbo_bible.setRenderer(new BookListCellRenderer());
        btn_go.setMnemonic('G');
        btn_go.setText("Go"); //$NON-NLS-1$
        btn_go.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev) { benchmark(); }
        });

        pnl_north.setLayout(new BorderLayout(5, 5));
        pnl_north.add(btn_go, BorderLayout.EAST);
        pnl_north.add(cbo_bible, BorderLayout.CENTER);

        txt_results.setColumns(30);
        txt_results.setRows(10);
        scr_results.getViewport().add(txt_results, null);

        this.setLayout(new BorderLayout(5, 5));
        this.add(pnl_north, BorderLayout.NORTH);
        this.add(scr_results, BorderLayout.CENTER);
    }

    /**
     * Show this Panel in a new dialog
     */
    public void showInDialog(Component parent)
    {
        showInDialog(parent, "Benchmark", false); //$NON-NLS-1$
    }

    /**
     * Run the benchmark on the selected Bible
     */
	protected void benchmark()
    {
        try
        {
            // This cast is safe because Bibles are filtered below
            Book book = mdl_bible.getSelectedBookMetaData().getBook();
    
            Speed speed = new Speed(book);
            speed.run();
    
            float time = speed.getBenchmark() / 1000;
            txt_results.append("Benchmark for '" + book.getBookMetaData().getName() + "': " + time + "s\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
            txt_results.append("Benchmark failed. No timing available.\n"); //$NON-NLS-1$
        }
    }

    /**
     * Combo model. The filter is important for the cast above
     */
    private BooksComboBoxModel mdl_bible = new BooksComboBoxModel(BookFilters.getBibles());

    private JPanel pnl_north = new JPanel();

    private JButton btn_go = new JButton();

    private JComboBox cbo_bible = new JComboBox();

    private JScrollPane scr_results = new JScrollPane();

    private JTextArea txt_results = new JTextArea();
}
