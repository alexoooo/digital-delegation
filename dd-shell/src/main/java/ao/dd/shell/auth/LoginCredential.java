package ao.dd.shell.auth;

import ao.dd.shell.def.TerminalAgent;
import ao.dd.shell.def.TransferAgent;
import ao.dd.shell.impl.ssh.SshShell;
import ao.dd.shell.impl.ssh.SshTransfer;
import ao.dd.shell.impl.transfer.ftp.apache.ApacheFtpAgent;
import ao.dd.shell.impl.transfer.ftps.glub.GlubFtpsAgent;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * User: aostrovsky
 * Date: 15-Jun-2009
 * Time: 10:06:49 AM
 */
public final class LoginCredential
{
    //-------------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(LoginCredential.class);


    //-------------------------------------------------------------------------
    private static final int PORT_SENTINEL = -1;


    //-------------------------------------------------------------------------
    public static void main(String[] args) throws IOException
    {
//        ShellAgent ssh = LoginCredential.full(
//                "192.168.1.103",
////                "alex-desktop",
//                "alex",
//                "xxx"
//        ).newShell();
//
//        ConsoleTerminalAudit auditor = new ConsoleTerminalAudit();
//        ssh.addAudit( auditor );
//
//        ssh.open();
//
//        System.out.println(
//                ssh.ls("."));
//
//        ssh.close();

//        new LoginChain(
//                LoginCredential.full(
//                        "x", "y", "z")/*,
//                LoginCredential.full(
//                        "x", "y", "z")*/
//        ).manageShell(
//                new Automator<ShellAgent>() {
//                    @Override public void automate(
//                            ShellAgent bot) throws Exception {
//
//                        String pwd = bot.exec("pwd");
//                        for (LsRecord ls : bot.ls(pwd))
//                        {
//                            System.out.println(ls + "\t" + ls.size());
//
//                            if (ls.isDirectory())
//                            {
//                                for (LsRecord subDir : bot.ls( pwd + "/" + ls.path() )) {
//                                    System.out.println(subDir + "\t" + subDir.size());
//                                }
//                            }
//                        }
//
////                        String local  = "x";
////                        String remote = "y";
////
////                        bot.upload(local, remote);
////                        LOG.info("same: " + ShellUtils.filesEqual(
////                                    bot, local, remote));
////                        bot.download(remote, local + ".dl");
////                        LOG.info("same: " + ShellUtils.filesEqual(
////                                    bot, local + ".dl", remote));
//
////                        SqlConsole sql =
////                                new SqlConsole(
////                                        "x", "cache/sql", bot);
////
////                        Table table = sql.query(
////                                "SELECT Count(*) FROM x " +
////                                    "LIMIT 000000000000000000000000001;");
////                        table.toCsv(System.out);
////                        sql.exitDb();
//                    }
//                });
    }


    //--------------------------------------------------------------------
    private final String host;
    private final int    port;
    private final String user;
    private final String pass;


    //--------------------------------------------------------------------
    public static LoginCredential local(String username)
    {
        assert username != null;
        return new LoginCredential(
                null, PORT_SENTINEL, username, null);
    }
    public static LoginCredential local(
            String username, String password)
    {
        assert username != null;
        assert password != null;
        return new LoginCredential(
                null, PORT_SENTINEL, username, password);
    }

    public static LoginCredential remote(String hostOrIp)
    {
        assert hostOrIp != null;
        return new LoginCredential(
                hostOrIp, PORT_SENTINEL, null, null);
    }
    public static LoginCredential remote(
            String hostOrIp, String password)
    {
        assert hostOrIp != null;
        assert password != null;
        return new LoginCredential(
                hostOrIp, PORT_SENTINEL, null, password);
    }

    public static LoginCredential full(
            String hostOrIp, String username, String password)
    {
        assert hostOrIp != null;
        assert username != null;
        assert password != null;
        return new LoginCredential(
                hostOrIp, PORT_SENTINEL, username, password);
    }
    public static LoginCredential full(
            String hostOrIp, int port, String username, String password)
    {
        assert hostOrIp != null;
        assert port     >= 0;
        assert username != null;
        assert password != null;
        return new LoginCredential(
                hostOrIp, port, username, password);
    }


    //--------------------------------------------------------------------
    private LoginCredential(
            String hostname,
            int    portNumber,
            String username,
            String password)
    {
        host = hostname;
        port = portNumber;
        user = username;
        pass = password;
    }


    //--------------------------------------------------------------------
    public String hostname() {  return host;  }
    public int    port    () {  return port;  }
    public String username() {  return user;  }
    public String password() {  return pass;  }


    //--------------------------------------------------------------------
    public TerminalAgent newShell()
    {
        return port == PORT_SENTINEL
               ? new SshShell(host, user, pass)
               : new SshShell(host, user, pass, port);
    }

    public TransferAgent newFtp()
    {
        return new ApacheFtpAgent(host, user, pass, false);
    }

    public TransferAgent newSftp()
    {
        return port == PORT_SENTINEL
               ? new SshTransfer(host, user, pass)
               : new SshTransfer(host, user, pass, port);
    }

    public TransferAgent newFtpsApache()
    {
        return new ApacheFtpAgent(host, user, pass, true);
    }

    public TransferAgent newFtpsGlub()
    {
        return port == PORT_SENTINEL
               ? new GlubFtpsAgent(host, user, pass)
               : new GlubFtpsAgent(host, port, user, pass);
    }

    
    //--------------------------------------------------------------------
    public void manageShell(Automator<TerminalAgent> automator)
    {
        TerminalAgent bot = newShell();
        try {
            bot.openChecked();
            automator.automate(bot);
        } catch (Exception e) {
             throw new Error( e );
        } finally {
            bot.close();
        }
    }

    public void manageSftp(Automator<TransferAgent> automator)
    {
        TransferAgent bot = newSftp();
        try {
            bot.openChecked();
            automator.automate(bot);
        } catch (Exception e) {
            throw new Error( e );
        } finally {
            bot.close();
        }
    }

    public void manageFtps(Automator<TransferAgent> automator)
    {
        TransferAgent bot = newFtpsApache();
        try {
            bot.openChecked();
            automator.automate(bot);
        } catch (Exception e) {
            throw new Error( e );
        } finally {
            bot.close();
        }
    }


    //--------------------------------------------------------------------
    public boolean logIn(AuthService service)
    {
        return service.logIn(host, user, pass);
    }


    //--------------------------------------------------------------------
    public boolean ssh(TerminalAgent with)
    {
        return ssh(with, null);
    }
    public boolean ssh(TerminalAgent with, Logger log)
    {
        return logIn(new SshAuthService(with, log));
    }

    public boolean scp(
            TerminalAgent with,
            String        destinationDirectory,
            String...     filesToUpload)
    {
        return logIn(new ScpAuthService(
                with, destinationDirectory, filesToUpload));
    }


    //--------------------------------------------------------------------
    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();

        if (user != null) {
            str.append(user);
        }

        if (user != null && host != null) {
            str.append('@');
        }

        if (host != null) {
            str.append(host);
        }

        return str.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoginCredential that = (LoginCredential) o;

        if (port != that.port) return false;
        if (pass != null ? !pass.equals(that.pass) : that.pass != null) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (pass != null ? pass.hashCode() : 0);
        return result;
    }


    //--------------------------------------------------------------------
    public static interface Automator<T>
    {
        public void automate(T bot) throws Exception;
    }

    public static interface AuthService
    {
        public boolean logIn(String host, String user, String pass);
    }
}
