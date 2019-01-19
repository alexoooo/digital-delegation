package ao.dd.shell.impl.transfer.ftps.glub;

import ao.dd.shell.ShellUtils;
import ao.dd.shell.def.ShellFile;
import ao.dd.shell.def.TransferAgent;
import ao.dd.shell.impl.transfer.stream.ThrottledInputStream;
import ao.dd.shell.impl.transfer.stream.ThrottledOutputStream;
import ao.util.io.Dirs;
import ao.util.time.Sched;
import ao.util.time.Stopwatch;
import com.glub.secureftp.bean.FTPException;
import com.glub.secureftp.bean.FTPNoSuchFileException;
import com.glub.secureftp.bean.RemoteFileList;
import com.glub.secureftp.bean.SSLFTP;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * User: aostrovsky
 * Date: 22-Jun-2009
 * Time: 12:16:05 PM
 * 
 * See http://www.glub.com/products/bean/api/index.html?help-doc.html
 * http://en.wikipedia.org/wiki/FTPS
 */
public class GlubFtpsAgent implements TransferAgent
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(GlubFtpsAgent.class);

    private static final long DEFAULT_PER_SEC =
            4 * 1000 * 1000 / 1000 / 8; // 4 mBits/s


    //--------------------------------------------------------------------
    static
    {
        // This call allows the java.security.SecureRandom object to be
        // generated prior to being used. Class SecureRandom provides a
        // cryptographically strong pseudo-random number generator (PRNG).
        // This secure random number generator is used by Glub Tech Secure
        // FTP Bean.
        SSLFTP.preSeed();
    }


    //--------------------------------------------------------------------
    public static void main(String[] args) throws Exception
    {
        // Perform the upload
        GlubFtpsAgent ftps = new GlubFtpsAgent(
        		args[0], args[1], args[2]
        );
        
        RemoteFileList frl = ftps.ftps.list();
        System.out.println(frl);

        // Prints if no exceptions
        ftps.close();
        System.out.println("Done.");
    }


    //--------------------------------------------------------------------
    private       SSLFTP  ftps;
    private       boolean throttle       = false;
    private       long    bytesPerSecond = DEFAULT_PER_SEC;

    private final String host;
    private final int    port;
    private final String user;
    private final String pass;

    private final TransferMode transferMode;


    //--------------------------------------------------------------------
    public GlubFtpsAgent(
            String hostname,
            String username,
            String password)
    {
        this(hostname, 990, username, password);
    }
    public GlubFtpsAgent(
            String hostname,
            int    portNumber,
            String username,
            String password)
    {
        host = hostname;
        port = portNumber;
        user = username;
        pass = password;

        transferMode = TransferMode.BINARY;
        checkMode();
    }


    //--------------------------------------------------------------------
    @Override
    public void throttle(long maxBytesPerSecond)
    {
        bytesPerSecond = maxBytesPerSecond;
        throttle();
    }
    
    public void throttle()
    {
        throttle = true;
    }

    @Override
    public void unThrottle()
    {
        throttle = false;
    }


    //--------------------------------------------------------------------
    @Override
    public ShellFile file(String remoteFilePath) {
        return fileInfo(remoteFilePath, true);
    }

    public ShellFile fileInfo(
            String remoteFilePath, boolean needDirInfo) {
        try {
            return getFileInfo(remoteFilePath, needDirInfo);
        } catch (FTPNoSuchFileException ignored) {
        } catch (Exception e) {
            LOG.warn("could not retrieve file info", e);
        }

        return null;
    }

    private ShellFile getFileInfo(
            String remoteFilePath, boolean needDirInfo)
            throws IOException, FTPException
    {
        return new ShellFile(
                remoteFilePath,
                ftps().size(remoteFilePath),
                needDirInfo && isDirectory(remoteFilePath),
                new DateTime(ftps().time(remoteFilePath).getTime()));
    }
    private boolean isDirectory(String remoteFilePath)
            throws IOException, FTPException {
        RemoteFileList subFiles = ftps().list(remoteFilePath);
        return subFiles != null && ! subFiles.isEmpty();
    }


    //--------------------------------------------------------------------
    @Override
    public List<ShellFile> files(String inRemoteFilePath)
    {
        try {
            return getFiles(inRemoteFilePath);
        } catch (Exception e) {
            LOG.trace("retrieving file info failed", e);
            return null;
        }
    }

    private List<ShellFile> getFiles(String inRemoteFilePath)
            throws IOException, FTPException
    {
        RemoteFileList files = ftps().list(inRemoteFilePath);
        List<ShellFile> infos = new ArrayList<ShellFile>();

        for (int i = 0; i < files.size(); i++) {
            String name = files.getFile(i).getFileName();
            if (name.equals(".") || name.equals("..")) {
                continue;
            }

            infos.add(new ShellFile(name, files.getFile(i)));
        }

        return infos;
    }


    //--------------------------------------------------------------------
    @Override
    public boolean makeDirs(String remoteDirPath)
    {
        return ShellUtils.makeDirs(this, remoteDirPath);
    }

    @Override
    public boolean makeDir(String remoteDirPath)
    {
        try
        {
            return doMakeDir(remoteDirPath);
        }
        catch (FTPException e)
        {
            if (e.getMessage().contains("already exists")) {
                return true;
            } else {
                LOG.error("creating directory failed", e);
            }
            return false;
        }
    }

    private boolean doMakeDir(
            String remoteDirPath) throws FTPException
    {
        ftps().mkdir(remoteDirPath);
        return true;
    }


    //--------------------------------------------------------------------
    @Override
    public boolean upload(String filePath, String toFilePath) {
        return upload(new File(filePath), toFilePath);
    }

    @Override
    public boolean upload(InputStream source, String toFilePath)
    {
        try {
            Stopwatch timer = new Stopwatch();
            doStore(source, toFilePath);
            LOG.debug("Uploaded " + source + " -> " + toFilePath +
                        " in " + timer);
            return true;
        } catch (Throwable t) {
            LOG.error("Upload " + source + " -> " + toFilePath + 
                        " failed", t);
            return false;
        }
    }

    @Override
    public boolean upload(File file, String toFilePath)
    {
        for (int trial = 0; trial < 10; trial++) {
            try {
                return doUpload(file, toFilePath);
            } catch (Throwable e) {
                LOG.error("Upload failed: " + e.getMessage());

                Sched.sleep(30 * 1000);
                LOG.info ("Retrying upload: " +
                            file + " -> " + toFilePath);

                close();
            }
        }
        return false;
    }

    private boolean doUpload(
            File file, String toFilePath) throws Throwable
    {
        Stopwatch timer = new Stopwatch();

        InputStream source = new FileInputStream(file);
        doStore(source, toFilePath);
        source.close();

        LOG.debug("Uploaded " + ShellUtils.transferDetails(
                    file.length(), timer.timingMillis()));
        return true;
    }

    private void doStore(
            final InputStream source, final String to) throws Throwable {
        final Throwable[]    transmissionError   = {null};
        final CountDownLatch transmissionStopped = new CountDownLatch(1);
//        final Condition transmissionStopped = new Condition(false);
        Thread run = new Thread(new Runnable() {public void run() {try {
            if (throttle) {
                InputStream in = new ThrottledInputStream(
                        source, bytesPerSecond,
                        new ThrottledInputStream.Monitor() {
                            public void progress(long remaining) {
                                if (remaining == 0 &&
                                        transmissionStopped.getCount() == 1) {
                                    LOG.debug("No more remaining");
                                    transmissionStopped.countDown();
                                }
                            }});
                try {
                    ftps().store(in, to);
                } finally {
                    in.close();
                }
            } else {
                ftps().store(source, to);
            }
        } catch (Throwable t) {
            transmissionError[0] = t;
        } finally {
            transmissionStopped.countDown();
        }}});

        run.start();
        transmissionStopped.await();
        Sched.sleep(1000);
        if (run.isAlive()) {
            Throwable error = transmissionError[0];

            LOG.warn("Upload hanging. {}", error);
            run.interrupt();
            LOG.warn("Interrupted hanging upload");
            run.join();
            LOG.warn("Hanging thread is dead");
            close();

            transmissionError[0] = error;
        }

        if (transmissionError[0] != null) {
            throw transmissionError[0];
        }
    }

    
    //--------------------------------------------------------------------
    @Override
    public boolean download(String fromFile, String toFile) {
        return download(fromFile, new File(toFile));
    }
    
    @Override
    public boolean download(String fromFileName, File to) {
        try {
            return doDownload(fromFileName, to);
        } catch (Exception e) {
            LOG.warn("download failed", e);
            return false;
        }
    }

    @Override
    public boolean download(String remoteFileName, OutputStream destination)
    {
        return false;
    }

    private boolean doDownload(String fromFileName, File to)
            throws IOException, FTPException
    {
        if (to.getParentFile() != null) {
            Dirs.get(to.getParentFile());
        }

        Stopwatch timer = new Stopwatch();

        if (throttle)
        {
            ThrottledOutputStream out =
                    new ThrottledOutputStream(to, bytesPerSecond);
            ftps().retrieve(fromFileName, out);
            out.close();
        }
        else
        {
            ftps().retrieve(fromFileName, to, true);
        }

        LOG.debug("downloaded " + ShellUtils.transferDetails(
                    to.length(), timer.timingMillis()));
        return true;
    }


    //--------------------------------------------------------------------
    private void checkMode()
    {
        try {
            doCheckMode();
        } catch (FTPException e) {
            LOG.error("checking transfer mode failed", e);
        }
    }
    private void doCheckMode() throws FTPException
    {
        // Transfer a file to the FTPS server in ASCII mode
        if (transferMode == TransferMode.ASCII) {
            ftps().ascii();
        } else {
            ftps().binary();
        }
    }


    //--------------------------------------------------------------------
    private SSLFTP ftps()
    {
        try
        {
            return ftpsChecked();
        }
        catch (IOException e)
        {
            LOG.error("Connection issue: " +
                    e.getClass() + " -> " + e.getMessage());
            return null;
        }
    }
    private SSLFTP ftpsChecked() throws IOException
    {
        if (ftps != null) return ftps;

        try
        {
            ftps = doFtpsConnect();
            return ftps;
        }
        catch (FTPException e)
        {
            LOG.error("FTPS connection failed: " +
                      e.getClass() + " -> " + e.getMessage());
            throw new Error( e );
        }
    }

    private SSLFTP doFtpsConnect() throws IOException, FTPException
    {
        SSLFTP sslftp = new SSLFTP(new SessionMan(),
                host, port, SSLFTP.IMPLICIT_CONNECTION,
                System.out, System.err);

        // Connect and login to FTPS server
        sslftp.connect(true);
        sslftp.login(user, pass, null);

        // Ensure that data is encrypted
        // in the communication with the FTPS server
        sslftp.setDataEncryptionOn(true);

        return sslftp;
    }


    //--------------------------------------------------------------------
    @Override
    public boolean open()
    {
        return ftps() != null;
    }

    @Override
    public void openChecked() throws IOException
    {
        ftpsChecked();
    }

    @Override
    public void close()
    {
        LOG.debug("logging out");
        if (ftps != null)
        {
            try {
                ftps.logout();
            } catch (Exception e) {
                LOG.error("ftps logout failed");
            }

            ftps = null;
        }
    }
}
