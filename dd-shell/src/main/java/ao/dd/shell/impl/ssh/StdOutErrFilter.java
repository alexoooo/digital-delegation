package ao.dd.shell.impl.ssh;

import ao.dd.shell.def.TerminalAgent;
import ao.util.math.Calc;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
* User: AO
* Date: 10/24/10
* Time: 5:23 PM
*/
class StdOutErrFilter extends FilterInputStream
{
    //------------------------------------------------------------------------
    public static StdOutErrFilter newOutFilter(
            List<TerminalAgent.Audit> auditors,
            InputStream         strOut)
    {
        return new StdOutErrFilter(
                auditors, OutAuditSelector.INSTANCE, strOut);
    }

    public static StdOutErrFilter newErrFilter(
            List<TerminalAgent.Audit> auditors,
            InputStream         stdErr)
    {
        return new StdOutErrFilter(
                auditors, ErrAuditSelector.INSTANCE, stdErr);
    }


    //------------------------------------------------------------------------
    private final List<TerminalAgent.Audit> auditors;
    private final AuditSelector             selector;

    private volatile boolean isEnabled = true;


    //------------------------------------------------------------------------
    private StdOutErrFilter(
            List<TerminalAgent.Audit> auditors,
            AuditSelector       selector,
            InputStream         in)
    {
        super( in );

        this.auditors = auditors;
        this.selector = selector;
    }


    //------------------------------------------------------------------------
    @Override
    public int read() throws IOException
    {
        int read = super.read();
        if (isEnabled && read >= 0) {
            for (TerminalAgent.Audit auditor : auditors) {
                selector.select( auditor ).accept( read );
            }
        }
        return read;
    }


    //------------------------------------------------------------------------
    @Override
    public int read(
            byte b[], int off, int len) throws IOException
    {
        int read = super.read(b, off, len);
        if (read > 0) {
            for (TerminalAgent.Audit auditor : auditors) {
                ByteAcceptor acceptor = selector.select( auditor );
                for (int i = 0; i < read; i++) {
                    acceptor.accept(
                            Calc.unsigned(b[off + i]));
                }
            }
        }
        return read;
    }


    //------------------------------------------------------------------------
    @Override
    public long skip(long bytes) throws IOException
    {
        if (isEnabled && ! auditors.isEmpty()) {
            throw new UnsupportedOperationException(
                    "Can't skip while being audited");
        } else {
            return super.skip(bytes);
        }
    }


    //------------------------------------------------------------------------
    public void setEnabled(boolean isEnabled)
    {
        this.isEnabled = isEnabled;
    }


    //------------------------------------------------------------------------
    private interface AuditSelector
    {
        public ByteAcceptor select(TerminalAgent.Audit audit);
    }
    private interface ByteAcceptor
    {
        public void accept(int byteValue);
    }

    private static class OutAuditSelector implements AuditSelector
    {
        public static final AuditSelector INSTANCE = new OutAuditSelector();

        @Override public ByteAcceptor select(final TerminalAgent.Audit audit) {
            return new ByteAcceptor() {
                @Override public void accept(int byteValue) {
                    audit.standardOutput( byteValue );
                }
            };
        }
    }

    private static class ErrAuditSelector implements AuditSelector
    {
        public static final AuditSelector INSTANCE = new ErrAuditSelector();

        @Override public ByteAcceptor select(final TerminalAgent.Audit audit) {
            return new ByteAcceptor() {
                @Override public void accept(int byteValue) {
                    audit.standardError(byteValue);
                }
            };
        }
    }
}
