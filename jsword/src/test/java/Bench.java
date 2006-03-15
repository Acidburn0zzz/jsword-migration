import java.util.List;

import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.test.Speed;

/**
 * Bench is a command line utility that runs the Speed benchmark program.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class Bench
{
    /**
     * Basic constructor
     */
    public static void main(String[] args)
    {
        Book version = null;

        if (args.length == 0)
        {
            usage();
            versions();
            System.exit(1);
        }

        try
        {
            List dicts = Books.installed().getBooks(BookFilters.getBibles());
            version = (Book) dicts.get(0);
        }
        catch (Exception ex)
        {
            Reporter.informUser(Bench.class, ex);

            System.out.println("Failed to load version '" + args[0] + "'"); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println("System message: " + ex); //$NON-NLS-1$
            System.out.println(""); //$NON-NLS-1$
            usage();
            System.exit(1);
        }

        Speed speed = new Speed(version);
        speed.run();

        try
        {
            float time = speed.getBenchmark() / 1000;
            System.out.println("CBench mark for '" + args[0] + "': " + time + "s"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        catch (Exception ex)
        {
            System.out.println("Benchmark failed. No timing available."); //$NON-NLS-1$
        }
    }

    /**
     * Print a usage message to stdout
     */
    private static void usage()
    {
        System.out.println("Usage: CBench [<version>] [disk]"); //$NON-NLS-1$
        System.out.println("  where <version> is the name of a version to benchmark."); //$NON-NLS-1$
        System.out.println("  and 'disk' specifies if the Raw version should not cache data."); //$NON-NLS-1$
        System.out.println("  Remember to quote the version name if it includes spaces."); //$NON-NLS-1$
    }

    /**
     * List the available versions
     */
    private static void versions()
    {
        System.out.println("  Available versions:"); //$NON-NLS-1$
        for (Book book : Books.installed().getBooks())
        {
            System.out.println("    " + book.getName()); //$NON-NLS-1$
        }
    }
}
