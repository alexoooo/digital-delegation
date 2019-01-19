package ao.dd.shell.auth;

import ao.dd.shell.ShellUtils;
import ao.dd.shell.def.TerminalAgent;
import ao.dd.shell.impl.terminal.data.StreamPartition;
import org.apache.log4j.Logger;

import java.io.InputStream;

/**
 * User: aostrovsky
 * Date: 8-Feb-2010
 * Time: 2:45:57 PM
 */
public class SshAuthService
        implements LoginCredential.AuthService
{
    //--------------------------------------------------------------------
    private final TerminalAgent agent;
    private final Logger        log;


    //--------------------------------------------------------------------
    public SshAuthService(TerminalAgent with)
    {
        this(with, null);
    }

    public SshAuthService(TerminalAgent with, Logger on)
    {
        agent = with;
        log   = on;
    }


    //------------------------------------------------------------------------
    @Override
    public boolean logIn(String hostOrIp, String user, String pass)
    {
        return logIn(hostOrIp, user, pass, 2 * 60 * 1000);
    }


    //------------------------------------------------------------------------
    public boolean logIn(
            String hostOrIp, String user, String pass, long timeout)
    {
        String userBefore = agent.exec("whoami");
        String hostBefore = ShellUtils.hostName (agent);
        String ipBefore   = ShellUtils.ipAddress(agent);

        ShellUtils.traceShell(agent, log);
        if (hostOrIp == null)
        {
            if (user == null)
            {
                return false;
            }

            agent.write("su - " +
                    ShellUtils.escapeArgument(user) + "\n");
        }
        else
        {
            agent.write("ssh " + ShellUtils.escapeArgument(
                    (user == null ? "" : user + "@") + hostOrIp) + "\n");
        }

        String ret = ShellUtils.traceShell(agent, log);

        if (ret.contains("yes/no")) {
            agent.write("yes\n");
            ret = ShellUtils.traceShell(agent, log);
        }

        // run out of re-tries
        while (ret.contains("Password:")) {
            boolean prevAuditEnabled = agent.setAuditEnabled(false);
            agent.write((pass == null ? "" : pass) + "\n");
            agent.setAuditEnabled( prevAuditEnabled );

            ret = ShellUtils.traceShell(agent, log);
        }

        // at this point, either:
        // 1) we successfully hopped
        // 2) the hopped failed
        // 3) the command is hanging

        InputStream userAfterStream = agent.execStream("whoami");
        String userAfter = StreamPartition.toString(userAfterStream, timeout);

        if (userAfter.isEmpty()) {
            // timed out
            return false;
        }

        String hostAfter = ShellUtils.hostName(agent);

        if (hostBefore.equals( hostAfter ) &&
            userBefore.equals( userAfter ) &&
                ! (hostBefore.equals( hostOrIp ) ||
                   ipBefore  .equals( hostOrIp )))
        {
             // could miss error during self-ssh
//            throw new Error("ssh from " +
//                    hostBefore + " to " +
//                    hostOrIp   + " as " +
//                    user       + " failed");
            return false;
        }

        return true;
    }
}
