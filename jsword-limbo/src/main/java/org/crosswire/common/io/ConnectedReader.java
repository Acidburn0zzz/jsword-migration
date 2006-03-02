package org.crosswire.common.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * ConnectedReader is for use in command line processing type applications
 * where it is important to flush and output to the user beofre we ask for
 * some input. This is a simplar concept to the C++ tie() method in the
 * iostreams classes.
 * @author Joe Walker
 */
public class ConnectedReader extends Reader
{
    /**
     *
     */
    public ConnectedReader(Reader in)
    {
        this.in = in;
    }

    /**
     *
     */
    public void tie(Writer outWriter)
    {
        this.out = outWriter;
    }

    /**
     * Override to pass out to the current Stream.
     * @return The byte read, as normal.
     */
    @Override
    public int read() throws IOException
    {
        if (out != null)
            out.flush();

        return in.read();
    }

    /**
     * Override to pass out to the current Stream.
     * @return The byte read, as normal.
     */
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException
    {
        if (out != null)
            out.flush();

        return in.read(cbuf, off, len);
    }

    /**
     * Shutdown
     */
    @Override
    public void close() throws IOException
    {
        in.close();
    }

    private Writer out = null;
    private Reader in = null;
}
