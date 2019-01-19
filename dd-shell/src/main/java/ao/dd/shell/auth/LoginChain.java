package ao.dd.shell.auth;

import ao.dd.shell.def.TerminalAgent;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: AO
 * Date: Sep 30, 2010
 * Time: 11:33:04 PM
 */
public final class LoginChain
{
    //------------------------------------------------------------------------
    private static final Logger LOG = Logger.getLogger(
            LoginChain.class);


    //------------------------------------------------------------------------
    private final List<LoginCredential> chain;


    //------------------------------------------------------------------------
    public LoginChain(
            LoginCredential... credentials)
    {
        this(Arrays.asList( credentials ));
    }

    public LoginChain(
            Iterable<LoginCredential> credentials)
    {
//        if (credentials == null) {
//            throw new NullPointerException("credentials");
//        }

        chain = new ArrayList<LoginCredential>();

        for (LoginCredential cred : credentials)
        {
            if (cred == null) {
                throw new NullPointerException(
                        credentials.toString());
            }

            chain.add( cred );
        }
    }


    //------------------------------------------------------------------------
    public String effectiveUser()
    {
        for (int i = chain.size() - 1; i >= 0; i--)
        {
            String userName = chain.get( i ).username();

            if (userName != null)
            {
                return userName;
            }
        }

        return null;
    }

    public String effectiveHost()
    {
        for (int i = chain.size() - 1; i >= 0; i--)
        {
            String hostName = chain.get( i ).hostname();

            if (hostName != null)
            {
                return hostName;
            }
        }

        return null;
    }

    public List<LoginCredential> credentials()
    {
        return Collections.unmodifiableList( chain );
    }


    //------------------------------------------------------------------------
    public TerminalAgent logIn()
    {
        TerminalAgent ssh = null;
        for (LoginCredential link : chain)
        {
            if (ssh == null)
            {
                LOG.trace("Starting shell at: " + link);
                ssh = link.newShell();
                if (! ssh.open()) {
                    LOG.trace("Initial connection failed: " + link);
                    return null;
                }
            }
            else
            {
                LOG.trace("SSH hopping to " + link);

                if (! link.ssh( ssh ))
                {
                    ssh.close();
                    LOG.warn("Hop failed from " + ssh + " to " + link);
                    return null;
                }
            }
        }
        return ssh;
    }

    public void manageShell(
            LoginCredential.Automator<TerminalAgent> automator)
    {
        TerminalAgent ssh = logIn();
        try {
            automator.automate( ssh );
        } catch (Exception e) {
             throw new Error( e );
        } finally {
            ssh.close();
        }
    }


    //------------------------------------------------------------------------
    @Override
    public String toString()
    {
        return chain.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoginChain that = (LoginChain) o;
        return chain.equals(that.chain);
    }

    @Override
    public int hashCode()
    {
        return chain.hashCode();
    }
}
