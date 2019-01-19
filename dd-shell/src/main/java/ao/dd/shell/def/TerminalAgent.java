package ao.dd.shell.def;

import ao.dd.shell.impl.terminal.file.LsRecord;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * User: aostrovsky
 * Date: 22-Jun-2009
 * Time: 12:40:30 PM
 *
 * Designed for character communication,
 *   for binary data, please use TransferAgent
 */
public interface TerminalAgent
        extends Closeable
{
    //--------------------------------------------------------------------
    String      exec      (String command);

    /**
     * @param command *nix command to execute.
     *          \n is automatically appended.
     *
     * @return Standard input returned by the command.
     *           Returned InputStream returns -1 when the
     *             command's input is done.
     *           It is <b>not</b> necessary to close this stream.
     */
    InputStream execStream(String command);


    //--------------------------------------------------------------------
    String write(
            String text,
            String stdOutAfter,
            String stdOutBefore);

    InputStream writeStream(
            String text,
            String stdOutAfter,
            String stdOutBefore);


    //--------------------------------------------------------------------
    void write(String text);


    //--------------------------------------------------------------------
    List<LsRecord> ls(String path);


    //--------------------------------------------------------------------
    OutputStream stdIn();

    InputStream  stdOut();

    InputStream  stdErr();


    //--------------------------------------------------------------------
    void addAudit   (Audit terminalAudit);

    void removeAudit(Audit terminalAudit);

    boolean setAuditEnabled(boolean isAuditEnabled);
    boolean isAuditEnabled();


    //--------------------------------------------------------------------
    boolean open();

    void openChecked() throws IOException;

    @Override
    void close();


    //------------------------------------------------------------------------
    interface Audit
    {
        void standardInput (int byteValue);
        void standardOutput(int byteValue);
        void standardError (int byteValue);
    }
}