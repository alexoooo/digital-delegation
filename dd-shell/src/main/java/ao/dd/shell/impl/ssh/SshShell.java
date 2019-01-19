package ao.dd.shell.impl.ssh;

import ao.dd.shell.def.TerminalAgent;
import ao.dd.shell.impl.terminal.data.StreamPartition;
import ao.dd.shell.impl.terminal.file.LsParser;
import ao.dd.shell.impl.terminal.file.LsRecord;
import ao.util.math.rand.Rand;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * User: aostrovsky
 * Date: 22-Jun-2009
 * Time: 2:12:24 PM
 */
public class SshShell implements TerminalAgent
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(SshShell.class);


    //--------------------------------------------------------------------
    private final SshConnection connection;


    //------------------------------------------------------------------------
    public SshShell(String hostOrIp,
                    String username,
                    String password)
    {
        this(SshConnection.create(hostOrIp, username, password));
    }
    public SshShell(String hostOrIp,
                    String username,
                    String password,
                    int    port)
    {
        this(SshConnection.create(hostOrIp, username, password, port));
    }

    public SshShell(SshConnection sshConnection)
    {
        if (sshConnection == null) {
            throw new NullPointerException( "sshConnection" );
        }

        connection = sshConnection;
    }


    //--------------------------------------------------------------------
    @Override
    public String exec(String command)
    {
        return StreamPartition.toString(
                 execStream(command), Long.MAX_VALUE
               ).trim();
    }

    @Override
    public InputStream execStream(String command)
    {
        String sentinelHead = String.valueOf(Rand.nextLong());
        String sentinelTail = String.valueOf(Rand.nextLong());

        String fullCommand =
                "echo \"" + sentinelHead + "\" ; " +
                "(" + command + ") ; "   +
                "echo \"" + sentinelTail + "\"\n";

        return writeStream(
                fullCommand,
                sentinelHead + "\r\n",
                sentinelTail + "\r\n");
    }

    
    //--------------------------------------------------------------------
    @Override
    public String write(
            String text,
            String stdOutAfter,
            String stdOutBefore)
    {
        return StreamPartition.toString(
                 writeStream(text, stdOutAfter, stdOutBefore),
                 Long.MAX_VALUE
               ).trim();
    }

    @Override
    public InputStream writeStream(
            String text, String stdOutAfter, String stdOutBefore)
    {
        LOG.debug("writing: " + text.trim());

        write(text);
        return StreamPartition.readBetween(
                 stdOut(), stdOutAfter, stdOutBefore, Long.MAX_VALUE);
    }


    //--------------------------------------------------------------------
    @Override
    public void write(String text)
    {
        try
        {
            connection.openShellChecked();
            doWrite(text);
        }
        catch (IOException e)
        {
            LOG.error("unable to write", e);
            throw new RuntimeException( e );
        }
    }

    private void doWrite(String text) throws IOException
    {
        connection.stdIn().write( text.getBytes() );
    }


    //--------------------------------------------------------------------
    @Override
    public List<LsRecord> ls(String path)
    {
        return new LsParser().parse(
                exec("ls -l " + path));
    }


    //--------------------------------------------------------------------
    @Override public OutputStream stdIn() {
        return connection.stdIn();
    }

    @Override public InputStream stdOut() {
        return connection.stdOut();
    }

    @Override public InputStream stdErr() {
        return connection.stdErr();
    }

    @Override
    public void addAudit(Audit terminalAudit)
    {
        connection.addAudit( terminalAudit );
    }

    @Override
    public void removeAudit(Audit terminalAudit)
    {
        connection.removeAudit( terminalAudit );
    }

    @Override
    public boolean setAuditEnabled(boolean isAuditEnabled)
    {
        return connection.setAuditEnabled( isAuditEnabled );
    }

    @Override
    public boolean isAuditEnabled()
    {
        return connection.isAuditEnabled();
    }


    //--------------------------------------------------------------------
    @Override
    public boolean open()
    {
        return connection.openShell();
    }

    @Override
    public void openChecked() throws IOException
    {
        connection.openShellChecked();
    }

    @Override
    public void close()
    {
        connection.close();
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        return "SSH Shell: " + connection;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SshShell sshShell = (SshShell) o;
        return connection.equals(sshShell.connection);
    }

    @Override
    public int hashCode()
    {
        return connection.hashCode();
    }
}
