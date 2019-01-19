package ao.dd.shell.sql;

import ao.dd.shell.ShellUtils;
import ao.dd.shell.def.TerminalAgent;
import ao.dd.shell.sql.driver.ConsoleDbParser;
import ao.dd.shell.sql.driver.MySqlResultParse;
import ao.dd.shell.sql.driver.MySqlUtils;
import ao.util.io.Dirs;
import ao.util.math.crypt.MD5;
import ao.util.persist.PersistentObjects;
import javolution.text.Text;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * User: aostrovsky
 * Date: 20-Jul-2009
 * Time: 7:22:14 AM
 */
public class SqlConsole
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(SqlConsole.class);


    //--------------------------------------------------------------------
    private final ConsoleDbParser.Factory DRIVER;
    private final TerminalAgent           SHELL;
    private final String                  LAUNCH_COMMAND;

    private       File                    cache;
    private       long                    cacheExpire;
    private       boolean                 connected;


    //--------------------------------------------------------------------
    public SqlConsole(
            String        launchCommand,
            TerminalAgent shell) {
        this(launchCommand, MySqlResultParse.FACTORY,
                (File) null, -1, shell);
    }
    public SqlConsole(
            String        launchCommand,
            String        cacheDir,
            TerminalAgent shell) {
        this(launchCommand, MySqlResultParse.FACTORY,
             Dirs.get(cacheDir), Long.MAX_VALUE, shell);
    }
    public SqlConsole(
            String        launchCommand,
            File          cacheDir,
            TerminalAgent shell) {
        this(launchCommand, MySqlResultParse.FACTORY,
             Dirs.get(cacheDir), Long.MAX_VALUE, shell);
    }
    public SqlConsole(
            String        launchCommand,
            String        cacheDir,
            long          cacheExpireMillis,
            TerminalAgent shell) {
        this(launchCommand, MySqlResultParse.FACTORY,
             Dirs.get(cacheDir), cacheExpireMillis,
             shell);
    }
    public SqlConsole(
            String        launchCommand,
            File          cacheDir,
            long          cacheExpireMillis,
            TerminalAgent shell) {
        this(launchCommand, MySqlResultParse.FACTORY,
                cacheDir, cacheExpireMillis, shell);
    }

    public SqlConsole(
            String                  launchCommand,
            ConsoleDbParser.Factory driver,
            TerminalAgent shell) {
        this(launchCommand, driver, (File) null, -1, shell);
    }
    public SqlConsole(
            String                  launchCommand,
            ConsoleDbParser.Factory driver,
            String                  cacheDir,
            TerminalAgent shell) {
        this(launchCommand, driver,
             Dirs.get(cacheDir), Long.MAX_VALUE,
             shell);
    }
    public SqlConsole(
            String                  launchCommand,
            ConsoleDbParser.Factory driver,
            File                    cacheDir,
            TerminalAgent shell) {
        this(launchCommand, driver,
             Dirs.get(cacheDir), Long.MAX_VALUE,
             shell);
    }
    public SqlConsole(
            String                  launchCommand,
            ConsoleDbParser.Factory driver,
            String                  cacheDir,
            long                    cacheExpireMillis,
            TerminalAgent shell) {
        this(launchCommand, driver,
             Dirs.get(cacheDir), cacheExpireMillis,
             shell);
    }

    public SqlConsole(
            String                  launchCommand,
            ConsoleDbParser.Factory driver,
            File                    cacheDir,
            long                    cacheExpireMillis,
            TerminalAgent           shell)
    {
        DRIVER         = driver;
        SHELL          = shell;
        LAUNCH_COMMAND = launchCommand;
        cache          = cacheDir;
        cacheExpire    = cacheExpireMillis;
    }


    //--------------------------------------------------------------------
    private void connect()
    {
        if (connected) return;
        connected = true;

        String logInRet = SHELL.write(
                LAUNCH_COMMAND + "\n", null, DRIVER.prompt());
        LOG.trace("sql log-in: " + logInRet);
        ShellUtils.traceShell(SHELL, LOG);
    }


    //--------------------------------------------------------------------
    public SqlConsole enableCache(String dir)
    {
        enableCache(Dirs.get(dir));
        return this;
    }
    public SqlConsole enableCache(File dir)
    {
        enableCache(dir, cacheExpire);
        return this;
    }
    public SqlConsole enableCache(File dir, long expirationMillis)
    {
        cache       = dir;
        cacheExpire = expirationMillis;

        return this;
    }

    public SqlConsole disableCache()
    {
        cache = null;
        return this;
    }


    //--------------------------------------------------------------------
    public Table query(
            String    sql,
            Object... parameters)
    {
    	return query(sql, toString(parameters));
    }
    public Table query(
            String    sql,
            String... parameters)
    {
        String query     = MySqlUtils.fillSqlVals(sql, parameters);
        File   cacheFile = null;

        if (cache != null && sql.matches("\\s*SELECT.*")) {
        	cacheFile = new File(
                    cache, MD5.hexDigest(query) + ".cache");
        	
            long age = System.currentTimeMillis()
                         - cacheFile.lastModified();
            if (age < cacheExpire) {
                Table cached = PersistentObjects.retrieve(cacheFile);
                if (cached != null) {
                    LOG.debug("retrieving cached (" +
                             cached.rowCount() + "): " + query);
                    return cached;
                }
            }
        }

        connect();

        //LOG.debug("query:\n" + query);
        Text ret = ShellUtils.toText(
                SHELL.writeStream(query + "\n", null, DRIVER.prompt()));
        ShellUtils.traceShell(SHELL, LOG);

        Table results = new Table(ret, DRIVER);

        if (cacheFile != null && ! results.isError()) {
            PersistentObjects.persist(results, cacheFile);
        }

        return results;
    }

    private static String[] toString(Object[] vals) {
    	String[] asString = new String[ vals.length ];
    	for (int i = 0; i < vals.length; i++) {
    		asString[ i ] = vals[ i ].toString();
    	}
    	return asString;
    }
    
    
    //--------------------------------------------------------------------
    public void use(String database)
    {
        connect();

        String command = "USE " + database + ";";
        LOG.trace("Running " + command);
        
        String useOutput = SHELL.write(
                command + "\n", null, DRIVER.prompt());

        LOG.trace(useOutput);
        ShellUtils.traceShell(SHELL, LOG);
    }


    //--------------------------------------------------------------------
    public void exitDb()
    {
        if (! connected) {
            return;
        }

        SHELL.write("exit;\n");
        ShellUtils.traceShell(SHELL, LOG);
    }

    public TerminalAgent terminal() {
        return SHELL;
    }
}
