package ao.dd.shell;

import ao.dd.shell.def.ShellFile;
import ao.dd.shell.def.TerminalAgent;
import ao.dd.shell.def.TransferAgent;
import ao.dd.shell.impl.terminal.data.StreamPartition;
import ao.util.math.crypt.MD5;
import ao.util.text.AoFormat;
import javolution.text.Text;
import javolution.text.TextBuilder;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: aostrovsky
 * Date: 15-Jun-2009
 * Time: 10:07:23 AM
 */
public class ShellUtils
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(ShellUtils.class);

    private static final String  NS_LOOKUP   =
            "/usr/sbin/nslookup";
    private static final Pattern ADDRESS_PAT = Pattern.compile(
            ".*Address:\\s+(\\S+).*", Pattern.DOTALL);
    private static final Pattern NAME_PAT    = Pattern.compile(
            ".*?name = ([^.]+)", Pattern.DOTALL);


    //--------------------------------------------------------------------
    private ShellUtils() {
        LOG.trace("init ShellUtils");
    }


    //--------------------------------------------------------------------
    public static String hostName(TerminalAgent ssh)
    {
        String host = "~";
        while (! host.matches("[\\w\\d]+"))
        {
            host = ssh.exec("hostname");
        }
        return host;
    }

    public static List<String> hostNames(
            TerminalAgent ssh, String ipAddress)
    {
        String  nsLookup = ssh.exec(
                  NS_LOOKUP + " " + ipAddress);
        Matcher match    = NAME_PAT.matcher(nsLookup);

        List<String> names = new ArrayList<String>();
        while (match.find()) {
            names.add(  match.group(1) );
        }
        return names;
    }

    public static String ipAddress(TerminalAgent ssh)
    {
        String  host     = hostName(ssh);
        String  nsLookup = ssh.exec(NS_LOOKUP + " " + host);
        Matcher match    = ADDRESS_PAT.matcher(nsLookup);
        if (match.matches()) {
            return match.group(1);
        } else {
            return null;
        }
    }


    //--------------------------------------------------------------------
    public static String traceShell(TerminalAgent of, Logger with)
    {
//        LOG.info("Flushing " + of + " with " + with);
        String stdOut = StreamPartition.toString(of.stdOut(), 1000);
        String stdErr = StreamPartition.toString(of.stdErr(), 1000);

        if (stdOut != null && stdOut.length() > 0)
        {
            if (with == null) {
                System.out.println("OUT: " + stdOut.trim());
            } else {
                with.trace("OUT: " + stdOut.trim());
            }
        }

        if (stdErr != null && stdErr.length() > 0)
        {
            if (with == null) {
                System.out.println("ERR: " + stdErr.trim());
            } else {
                with.trace("ERR: " + stdErr.trim());
            }
        }

        return (String.valueOf(stdOut).trim() + "\n" +
                String.valueOf(stdErr).trim());
    }


    //--------------------------------------------------------------------
    public static String escapeArgument(String shellArgument)
    {
        if (shellArgument.matches("^[\\w\\d]+$")) {
            return shellArgument;
        }

        return "'" +
                shellArgument.replaceAll("'", "\\'") +
               "'";
    }

    public static String escapeCommand(String shellCommand)
    {
        if (shellCommand.matches("^[\\w\\d]+$")) {
            return shellCommand;
        }

        return shellCommand.replaceAll(
                "([#&;`|*?~<>^()\\[\\]\\{\\}$,\\x0A\\xFF'\"])",
                "\\$1");
    }
    

    //--------------------------------------------------------------------
    public static boolean filesEqual(
            TerminalAgent client,
            String        localFilePath,
            String        remoteFilePath)
    {
        return filesEqual(client,
                new File(localFilePath), remoteFilePath);
    }
    
    public static boolean filesEqual(
            TerminalAgent client,
            File          localFile,
            String        remoteFilePath)
    {
        try {
            return doFilesEqual(client, localFile, remoteFilePath);
        } catch (Exception e) {
            LOG.error("files identical check failed", e);
            return false;
        }
    }

    private static boolean doFilesEqual(
            final TerminalAgent client,
            final File          localFile,
            final String        remoteFilePath)
                    throws IOException, InterruptedException
    {
        LOG.debug("testing equality " +
                  localFile + " vs " + remoteFilePath);

        final long localSize = localFile.length();

        List<? extends ShellFile> stats = client.ls(remoteFilePath);
        List<? extends ShellFile> stat  =
                (stats.isEmpty() ? null : stats);
        long remoteSize = (stat == null || stat.isEmpty())
                          ? -1 : stats.get(0).size();
        if (localSize != remoteSize) return false;

        final String localHash[] = new String[1];
        Thread digester = new Thread(new Runnable() {
            public void run() {
                MD5 digest = new MD5();
                digest.feed(localFile, localSize);
                localHash[0] = digest.hexDigest();
            }
        });
        digester.start();

        String remoteHash = client.exec(
                "digest -a md5 " + remoteFilePath);
        digester.join();

        String   localDigest = localHash[0];
        String  remoteDigest = remoteHash.trim();
        boolean        equal =
                localDigest.equalsIgnoreCase( remoteDigest );

        LOG.debug("hashes: " + localDigest + " vs " + remoteDigest +
                    " (" + (equal ? "equal" : "different") + ")");
        return equal;
    }


    //--------------------------------------------------------------------
    public static boolean makeDirs(
            TransferAgent bot, String paths)
    {
        boolean      exists   = false;
        List<String> fullPath = fullPaths(paths);
        for (int i = fullPath.size() - 1; i >= 0; i--) {
            if (bot.makeDir(fullPath.get(i))) {
                exists = true;
                for (int j = i + 1; j < fullPath.size(); j++) {
                    exists = bot.makeDir(fullPath.get(j));
                }
                break;
            }
        }
        return exists;
    }

    private static List<String> fullPaths(String ofFile)
    {
        List<String> fullPaths = new ArrayList<String>();

        StringBuilder path = new StringBuilder();
        if (ofFile.startsWith("/")) {
            path.append("/");
        }

        for (String dir : ofFile.split("/+"))
        {
            if (dir.isEmpty()) continue;
            path.append(dir);
            fullPaths.add(path.toString());
            path.append("/");
        }

        return fullPaths;
    }



    //--------------------------------------------------------------------
    public static String transferDetails(
            long size, long timing)
    {
        if (size == -1) {
            return "took " + AoFormat.hhmmss(timing);
        }

        long timingSeconds = (timing / 1000);
        long speed         =
                (timingSeconds == 0)
                ? size
                : size / timingSeconds;
        return AoFormat.decimal(size) + " bytes" +
               " in " + AoFormat.hhmmss(timing)  +
               " at " + AoFormat.decimal(speed)  + " B/s";
    }


    //--------------------------------------------------------------------
    public static Text toText(InputStream in) {
        return toText(in, Long.MAX_VALUE);
    }
    public static Text toText(InputStream in, long timeout) {
        try {
            return readToText(in, timeout);
        } catch (IOException e) {
            LOG.error("Unable to slurp in stream", e);
            return null;
        }
    }
    private static Text readToText(
            InputStream in, long timeout) throws IOException
    {
        TextBuilder str   = new TextBuilder();
        long        start = System.currentTimeMillis();
        while (true) {
            int val = StreamPartition.readIfAvailable(in, start, timeout);
            if (val == -1) break;
            str.append((char) val);
        }
        return str.toText();
    }

    public static String toString(InputStream in) {
        return toString(in, Long.MAX_VALUE);
    }

    public static String toString(
            InputStream in, long timeout) {
        try {
            return readToString(in, timeout);
        } catch (IOException e) {
            LOG.error("Unable to read in stream", e);
            return null;
        }
    }
    private static String readToString(
            InputStream in,
            long        timeout) throws IOException
    {
        StringBuilder str   = new StringBuilder();
        long          start = System.currentTimeMillis();
        while (true) {
            int val = StreamPartition.readIfAvailable(in, start, timeout);
            if (val == -1) break;
            str.append((char) val);
        }
        return str.toString();
    }
}
