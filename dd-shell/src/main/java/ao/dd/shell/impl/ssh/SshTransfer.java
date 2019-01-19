package ao.dd.shell.impl.ssh;

import ao.dd.shell.ShellUtils;
import ao.dd.shell.def.ShellFile;
import ao.dd.shell.def.TransferAgent;
import ao.dd.shell.impl.transfer.stream.ThrottledInputStream;
import ao.dd.shell.impl.transfer.stream.ThrottledOutputStream;
import ao.util.async.Throttle;
import ao.util.io.Dirs;
import ao.util.text.AoFormat;
import ao.util.time.Stopwatch;
import ch.ethz.ssh2.SFTPException;
import ch.ethz.ssh2.SFTPv3DirectoryEntry;
import ch.ethz.ssh2.SFTPv3FileAttributes;
import ch.ethz.ssh2.SFTPv3FileHandle;
import ch.ethz.ssh2.sftp.ErrorCodes;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: aostrovsky
 * Date: 22-Jun-2009
 * Time: 1:18:49 PM
 * 
 * http://en.wikipedia.org/wiki/SSH_File_Transfer_Protocol
 */
public class SshTransfer implements TransferAgent
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(SshTransfer.class);


    //--------------------------------------------------------------------
    private final SshConnection connection;
    private       long          throttle = -1;


    //--------------------------------------------------------------------
    public SshTransfer(String hostOrIp,
                       String username,
                       String password)
    {
        this(SshConnection.create(hostOrIp, username, password));
    }
    public SshTransfer(String hostOrIp,
                       String username,
                       String password,
                       int    port)
    {
        this(SshConnection.create(hostOrIp, username, password, port));
    }

    public SshTransfer(SshConnection sshConnection)
    {
        connection = sshConnection;
    }


    //--------------------------------------------------------------------
    @Override
    public void throttle(long maxBytesPerSecond)
    {
        throttle = maxBytesPerSecond;
    }

    @Override
    public void unThrottle()
    {
        throttle = -1;
    }


    //--------------------------------------------------------------------
    @Override
    public ShellFile file(String remoteFilePath)
    {
        try
        {
            return new ShellFile(
                    remoteFilePath,
                    connection.sftp().stat(remoteFilePath));
        }
        catch (IOException e)
        {
            if (! e.getMessage().contains(
                    "SSH_FX_NO_SUCH_FILE")) {
                LOG.error("Retrieving file info failed", e);
            }
            return null;
        }
    }


    //--------------------------------------------------------------------
    @Override
    public List<ShellFile> files(String inRemoteFilePath)
    {
        try {
            return getFiles(inRemoteFilePath);
        } catch (SFTPException e) {
            if (e.getServerErrorCode() !=
                    ErrorCodes.SSH_FX_NO_SUCH_FILE) {
                LOG.error("listing files failed", e);
            }
        } catch (IOException e) {
            LOG.error("unexpected error", e);
        }
        return null;
    }

    private List<ShellFile> getFiles(
            String inRemoteFilePath) throws IOException
    {
        List<ShellFile> 	       files   = new ArrayList<ShellFile>();

        @SuppressWarnings("unchecked")
        List<SFTPv3DirectoryEntry> entries =
        	connection.sftp().ls(inRemoteFilePath);

        for (SFTPv3DirectoryEntry entry : entries) {            
            if (entry.filename.equals(".") ||
                entry.filename.equals("..")) {
                continue;
            }

            files.add(new ShellFile(
                    entry.filename,
                    entry.attributes));
        }

        return files;
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
        try {
            return doMakeDir(remoteDirPath);
        } catch (SFTPException e) {
            if (e.getServerErrorCode() ==
                    ErrorCodes.SSH_FX_FAILURE) {
                return true;
            } else if (e.getServerErrorCode() !=
                    ErrorCodes.SSH_FX_NO_SUCH_FILE) {
                LOG.error("make folders failed", e);
            }
        } catch (IOException e) {
            LOG.error("unexpected error", e);
        }
        return false;
    }

    private boolean doMakeDir(
            String remoteDirPath) throws IOException
    {
        connection.sftp().mkdir(
                remoteDirPath, 493 /*0755*/ );
        return true;
    }


    //--------------------------------------------------------------------
    @Override
    public boolean upload(
            String filePath, String toFilePath) {
        return upload(new File(filePath), toFilePath);
    }

    @Override
    public boolean upload(
            File file, String toFilePath)
    {
        if (! (file.exists() && file.canRead())) {
            LOG.debug("can't upload invalid file: " + file);
            return false;
        }

        InputStream in = null;
        try
        {
            in = new FileInputStream(file);
            doUpload(in, file.toString(), file.length(), toFilePath);
            return true;
        }
        catch (IOException e)
        {
            LOG.error("upload failed", e);
            return false;
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                LOG.error("unable to close local file " + file);
            }
        }
    }

    @Override
    public boolean upload(InputStream source, String toFilePath)
    {
        try {
            doUpload(source, source.toString(), -1, toFilePath);
            return true;
        } catch (IOException e) {
            LOG.error("Error while uploading: " +
                        source + " -> " + toFilePath, e);
            return false;
        }
    }

    private void doUpload(
            InputStream source,
            String      sourceName,
            long        sourceLength,
            String      toFilePath) throws IOException
    {
        Stopwatch timer = new Stopwatch();
        LOG.debug("uploading " + sourceName + " to " + toFilePath);
        SFTPv3FileHandle out =
                connection.sftp().createFileTruncate(toFilePath);

        InputStream in;
        if (throttle == -1 || throttle == Integer.MAX_VALUE) {
            if (! (source instanceof BufferedInputStream)) {
                in = new BufferedInputStream(source);
            } else {
                in = source;
            }
        } else {
            in = new ThrottledInputStream(source, throttle);
        }

        byte buffer[] = new byte[32768]; // max size
        long offset   = 0;

        int     chunk           = 0;
        int     chunks          = (int)
                Math.ceil(((double) sourceLength) / buffer.length);
        int     progressChunks  = chunks / 100;
        boolean displayProgress = (progressChunks > 10);

        while (true)
        {
            int ret = in.read(buffer);
            if (ret == -1) {
                break;
            }

            connection.sftp().write(out, offset, buffer, 0, ret);

            offset += ret;

            if (displayProgress && (chunk++ % progressChunks == 0)) {
                System.out.print(".");
            }
        }
        if (displayProgress) {
            System.out.println();
        }

        in.close();
        connection.sftp().closeFile(out);

        LOG.debug("uploaded " + ShellUtils.transferDetails(
                    sourceLength, timer.timingMillis()));
    }
    

    //--------------------------------------------------------------------
    @Override
    public boolean download(
            String fromFile, String toFile) {
        return download(fromFile, new File(toFile));
    }

    @Override
    public boolean download(String fromFilename, OutputStream to) {
        return download(fromFilename, to, to.toString());
    }

    @Override
    public boolean download(
            String fromFilename, File to)
    {
        if (to.getParentFile() != null) {
            Dirs.get(to.getParentFile());
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(to);
            return download(fromFilename, out, to.toString());
        } catch (IOException e) {
            LOG.error("download destination invalid: " + to, e);
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOG.error("unable to close download destination: " +
                                to, e);
                }
            }
        }
    }

    private boolean download(
            String       fromFilename,
            OutputStream to,
            String       toName)
    {
        try {
            doDownload(fromFilename, to, toName);
            return true;
        } catch (IOException e) {
            LOG.error("download failed", e);
            return false;
        }
    }

    private void doDownload(
            String       fromFilename,
            OutputStream to,
            String       toName) throws IOException
    {
        Stopwatch timer = new Stopwatch();
        LOG.debug("downloading " + fromFilename + " to " + toName);

        SFTPv3FileHandle in =
                connection.sftp().openFileRO(fromFilename);

        SFTPv3FileAttributes info = connection.sftp().fstat(in);

        byte buffer[] = new byte[32768]; // max size
        long offset   = 0;
        long remain   = info.size;

        int     chunk           = 0;
        int     chunks          = (int)
                Math.ceil(((double) info.size) / buffer.length);
        int     progressChunks  = chunks / 100;
        boolean displayProgress = (progressChunks > 10);

        OutputStream out =
                throttle == -1 || throttle == Integer.MAX_VALUE
                ? new BufferedOutputStream(to)
                : new ThrottledOutputStream(to, throttle);

        LOG.debug("Starting download of " +
                  AoFormat.decimal(info.size) + " bytes " +
                  "throttled at " + new Throttle(throttle));

        while (remain > 0)
        {
            long read = Math.min(buffer.length, remain);
            int  ret  = connection.sftp().read(
                    in, offset, buffer, 0, (int) read);
            if (ret != read) {
                throw new IOException(
                        "incorrect amount read: " + read + " vs " + ret);
            }
            offset += ret;
            remain -= ret;

            out.write(buffer, 0, ret);

            if (displayProgress && (chunk++ % progressChunks == 0)) {
                if (LOG.isDebugEnabled()) {
                    System.out.print(".");
                }
            }
        }
        if (displayProgress && LOG.isDebugEnabled()) {
            System.out.println();
        }

        out.close();
        connection.sftp().closeFile(in);

        LOG.info("downloaded " + ShellUtils.transferDetails(
                    info.size, timer.timingMillis()) + " to " + toName);
    }


    //--------------------------------------------------------------------
    @Override
    public boolean open()
    {
        return connection.openSftp();
    }

    @Override
    public void openChecked() throws IOException
    {
        connection.openSftpChecked();
    }

    @Override
    public void close()
    {
        connection.close();
    }
}
