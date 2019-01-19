package ao.dd.shell.auth;

import ao.dd.shell.ShellUtils;
import ao.dd.shell.def.TerminalAgent;
import ao.dd.shell.impl.terminal.data.StreamPartition;
import ao.util.math.rand.Rand;
import ao.util.pass.Filter;
import org.apache.log4j.Logger;

/**
 * User: aostrovsky
 * Date: 8-Feb-2010
 * Time: 2:56:08 PM
 */
public class ScpAuthService
        implements LoginCredential.AuthService
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger( ScpAuthService.class );


    //--------------------------------------------------------------------
    private final TerminalAgent agent;
//    private final Logger        log;

    private final String        destination;
    private final String[]      files;


    //--------------------------------------------------------------------
    public ScpAuthService(
            TerminalAgent with,
            String        destinationDir,
            String...     filesToUpload)
    {
//        this(with, destinationDir, filesToUpload, null);
//    }
//
//    public ScpAuthService(
//            TerminalAgent with,
//            String        destinationDir,
//            String[]      filesToUpload,
//            Logger        on)
//    {
        agent = with;
//        log   = on;

        destination = destinationDir;
        files       = filesToUpload;
    }


    //-------------------------------------------------------------------------
    @Override
    public boolean logIn(
            final String host,
            final String user,
            final String pass)
    {
        String sentinel     = String.valueOf(Rand.nextLong());
        String sentinelLine = sentinel + "\r\n";

        String scpDestination =
                (user == null ? "" : user + "@") +
                    host + ":" + destination;

        StringBuilder cmd = new StringBuilder("scp ");
        for (String localFile : files)
        {
            cmd = cmd.append(localFile).append(" ");
        }
        cmd = cmd.append(scpDestination)
                 .append(" ; echo \"")
                 .append(sentinel)
                 .append("\"\n");

        ShellUtils.traceShell(agent, LOG);
        agent.write( cmd.toString() );

        StreamPartition.scan(
                agent.stdOut(),
                new Filter<String>() {
                    @Override public boolean accept(String instance) {
                        if (instance.equals("yes/no")) {
                            agent.write("yes\n");
                        } else if (instance.equals("Password:")) {
                            boolean prevAuditEnabled =
                                    agent.setAuditEnabled( false );
                            agent.write((pass == null ? "" : pass) + "\n");
                            agent.setAuditEnabled( prevAuditEnabled );
                        } else /*if (instance.equals(sentinelLine))*/ {
                            return true;
                        }
                        return false;
                    }},
                "yes/no", "Password:", sentinelLine
        );

//        InputStream is = agent.writeStream(
//                cmd.toString(), null, sentinel + "\r\n");
//
//        String ret;
//        ret = StreamPartition.get().toString(
//                        agent.stdOut(), 3000);
//        LOG.debug("got response: " + ret);
//        if (ret.contains("yes/no"))
//        {
//            agent.write("yes\n");
//            ret = StreamPartition.get().toString(
//                        agent.stdOut(), 3000);
//            LOG.debug("got response: " + ret);
//        }
//
//        if (ret.contains("Password:"))
//        {
//            agent.write((pass == null ? "" : pass) + "\n");
//        }

        // ok, we're authenticated.

        // Because scp sends a bunch of gobbledygook control
        // characters and \b's in order to print its pretty
        // progress meter, we want to just read until we grab
        // our EOF sentinel.

//        try {
//            int nextByte;
//            while ((nextByte = is.read()) != -1) {
//                System.out.print((char) nextByte);
//            }
//        } catch (IOException e) {
//            LOG.error("Unable to read SCP output", e);
//            return false;
//        }

        return true;
    }
}
