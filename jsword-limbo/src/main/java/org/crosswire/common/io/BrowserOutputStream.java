package org.crosswire.common.io;

import java.applet.AppletContext;
import java.io.OutputStream;

/**
 * BrowserOutputStream allows output to the status line of a Browser This
 * probably would be more useful as a BrowserWriter
 * 
 * @author Joe Walker
 */
public class BrowserOutputStream extends OutputStream {
    /**
     * Display in the Browser Status bar
     * 
     * @param new_appcx
     *            The Brower Context
     */
    public BrowserOutputStream(AppletContext new_appcx) {
        appcx = new_appcx;
    }

    /**
     * The standard write mathod
     * 
     * @param b
     *            The byte to be written, as normal.
     */
    @Override
    public void write(int b) {
        char ch = (char) b;

        if (ch == '\n' || ch == '\r') {
            text = "";
        } else {
            text += ch;
            appcx.showStatus(text);
        }
    }

    private AppletContext appcx;
    private String text = "";
}
