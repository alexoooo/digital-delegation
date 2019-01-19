package ao.dd.shell.impl.ssh;

import ao.dd.common.WebUtils;
import ao.dd.shell.def.TerminalAgent;
import ao.util.serial.Serializer;
import ao.util.text.Txt;
import ch.ethz.ssh2.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * User: aostrovsky
 * Date: 22-Jun-2009
 * Time: 2:16:48 PM
 */
public class SshConnection
{
    //--------------------------------------------------------------------
    private static final Logger LOG          =
            Logger.getLogger(SshConnection.class);

    private static final int    TIMEOUT      = 30 * 1000;
    private static final int    DEFAULT_PORT = 22;



    //--------------------------------------------------------------------
    public static SshConnection create(
            String hostOrIp, String username, String password)
    {
        return createWithoutNsLookup(
                WebUtils.nsLookup(hostOrIp), username, password);
    }
    public static SshConnection create(
            String hostOrIp, String username, String password, int port)
    {
        return createWithoutNsLookup(
                WebUtils.nsLookup(hostOrIp), username, password, port);
    }

    public static SshConnection createWithoutNsLookup(
            String hostname, String username, String password)
    {
        return new SshConnection(hostname, username, password, DEFAULT_PORT);
    }
    public static SshConnection createWithoutNsLookup(
            String hostname, String username, String password, int port)
    {
        return new SshConnection(hostname, username, password, port);
    }


    //--------------------------------------------------------------------
    private final List<TerminalAgent.Audit> auditors =
            new CopyOnWriteArrayList<TerminalAgent.Audit>();

    private final String    hostName;
    private final String    userName;
    private final String    password;
    private final int       port;

    private Connection      connection;

    private SFTPv3Client    sftp;

    private Session         session;
    private StdInFilter     stdIn;
    private StdOutErrFilter stdOut;
    private StdOutErrFilter stdErr;
    private boolean         shellOpened;
    private boolean         auditEnabled = true;


    //--------------------------------------------------------------------
    private SshConnection(String hostname,
                          String username,
                          String password,
                          int    port)
    {
        this.hostName = hostname;
        this.port     = port;
        this.userName = username;
        this.password = password;
    }


    //--------------------------------------------------------------------
    public OutputStream stdIn()
    {
        if (stdIn == null) {
            openSession();
        }
        return stdIn;
    }

    public InputStream stdOut()
    {
        if (stdOut == null) {
            openSession();
        }
        return stdOut;
    }

    public InputStream stdErr()
    {
        if (stdErr == null) {
            openSession();
        }
        return stdErr;
    }


    //--------------------------------------------------------------------
    public void addAudit(TerminalAgent.Audit terminalAudit)
    {
        auditors.add( terminalAudit );
    }

    public void removeAudit(TerminalAgent.Audit terminalAudit)
    {
        auditors.remove(terminalAudit);
    }

    public boolean setAuditEnabled(boolean isAuditEnabled)
    {
        if (stdIn != null)
        {
            stdIn .setEnabled(isAuditEnabled);
            stdOut.setEnabled(isAuditEnabled);
            stdErr.setEnabled(isAuditEnabled);
        }
        boolean previousEnabledStatus = auditEnabled;
        auditEnabled = isAuditEnabled;
        return previousEnabledStatus;
    }

    public boolean isAuditEnabled()
    {
        return auditEnabled;
    }


    //--------------------------------------------------------------------
    public synchronized SFTPv3Client sftp()
    {
        if (sftp == null) {
            openSftp();
        }
        return sftp;
    }

    public boolean openSftp()
    {
        try {
            openSftpChecked();
            return false;
        } catch (IOException e) {
            LOG.error("SFTP client failed to open", e);
            return true;
        }
    }
    public void openSftpChecked() throws IOException
    {
        LOG.debug("openning SFTP client");
        sftp = new SFTPv3Client(connection()/*, System.out*/);
    }

    private void closeSftp()
    {
        if (sftp != null) {
            LOG.debug("closing SFTP client");
            sftp.close();
            sftp = null;
        }
    }


    //--------------------------------------------------------------------
    public synchronized Session session()
    {
        if (session == null) {
            openSession();
        }
        return session;
    }

    private void openSession()
    {
        try
        {
            doOpenSession();
        }
        catch (IOException e)
        {
            LOG.error("establish session failed", e);
            throw new Error( e );
        }
    }
    private void doOpenSession() throws IOException
    {
        assert session == null : "Session already open";
        LOG.debug("opening session");

        session = connection().openSession();

        stdIn  = new StdInFilter(auditors, session.getStdin() );
        stdOut = StdOutErrFilter.newOutFilter(
                auditors, new StreamGobbler(session.getStdout()));
        stdErr = StdOutErrFilter.newErrFilter(
                    auditors, new StreamGobbler( session.getStderr() ));

        setAuditEnabled( auditEnabled );

//        // consume greeting message
//        LOG.trace(SshShell.read(stdErr(), GIBERISH, GIBERISH, 5000));
//        LOG.trace(SshShell.read(stdOut(), GIBERISH, GIBERISH, 5000));
    }

    private void closeSession()
    {
        if (session != null)
        {
            LOG.debug("closing session");
            session.close();

            session     = null;
            stdIn       = null;
            stdOut      = null;
            stdErr      = null;
            shellOpened = false;
        }
    }


    //--------------------------------------------------------------------
    public boolean openShell()
    {
        try {
            openShellChecked();
            return true;
        } catch (IOException e) {
            LOG.debug("opening shell failed", e);
            return false;
        }
    }

    public synchronized void openShellChecked() throws IOException
    {
        if (shellOpened) return;
        shellOpened = true;

        session().requestDumbPTY();
        session().startShell();
    }


    //--------------------------------------------------------------------
    public synchronized Connection connection()
    {
        if (connection == null) {
            if (! openConnection(hostName, userName, password)) {
                throw new Error(
                        "could not open connection to: " + hostName);
            }
        }
        return connection;
    }

    private boolean openConnection(
            String hostname,
            String username,
            String password)
    {
        try
        {
            return doOpenConnection(hostname, username, password);
        }
        catch (Exception e)
        {
//            LOG.error("log-in failed", e);
            return false;
        }
    }

    private boolean doOpenConnection(
            String hostname, String username, final String password)
                    throws IOException
    {
        assert connection == null
                : "already logged-in";

        LOG.debug("connecting to " + hostname);
        connection = new Connection(hostname, port);
        ConnectionInfo info = connection.connect(
                null, TIMEOUT, TIMEOUT);
        LOG.trace("connected with: " + Serializer.toXml(info));

        LOG.debug("authenticating " + username + "/" +
        			Txt.nTimes("*", password.length()));

        if (connection.isAuthMethodAvailable(username, "password")) {
            return connection.authenticateWithPassword(username, password);
        } else if (connection.isAuthMethodAvailable(
                username, "keyboard-interactive")) {
            return connection.authenticateWithKeyboardInteractive(
                    username, new InteractiveCallback() {
                        @Override public String[] replyToChallenge(
                                String name, String instruction,
                                int numPrompts, String[] prompt,
                                boolean[] echo) throws Exception {
                            String[] response = new String[ numPrompts ];
                            for (int i = 0; i < response.length; i++) {
                                response[ i ] = password;
                            }
                            return response;
                        }
                    });
        }
        else
        {
            throw new Error("Unsupported Method: " + Arrays.toString(
                    connection.getRemainingAuthMethods(username)));
        }
    }

    private void closeConnection()
    {
        if (connection != null)
        {
            LOG.debug("closing connection");
            connection.close();
            connection = null;
        }
    }


    //--------------------------------------------------------------------
    public void close()
    {
        try {
            if (stdIn != null ||
                stdOut != null ||
                stdErr != null)
            {
                stdIn() .close();
                stdOut().close();
                stdErr().close();
            }
        } catch (IOException e) {
            LOG.error("Unable to close socket", e);
        }

        closeSftp();
        closeSession();
        closeConnection();
    }

    public boolean isOpen()
    {
        return (session != null);
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return String.valueOf(userName) + "@" +
               String.valueOf(hostName);
    }
}
