package ao.dd.shell.impl.ssh;

import ao.dd.shell.def.TerminalAgent;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
* User: AO
* Date: 10/24/10
* Time: 5:21 PM
*/
class StdInFilter extends FilterOutputStream
{
    //------------------------------------------------------------------------
    private final List<TerminalAgent.Audit> auditors;

    private volatile boolean isEnabled = true;


    //------------------------------------------------------------------------
    public StdInFilter(List<TerminalAgent.Audit> auditors, OutputStream out)
    {
        super( out );
        this.auditors = auditors;
    }


    //------------------------------------------------------------------------
    @Override public void write(int b) throws IOException
    {
        if (isEnabled) {
            for (TerminalAgent.Audit auditor : auditors)
            {
                auditor.standardInput(b);
            }
        }

        super.write( b );
    }

    @Override
    public void write(
            byte b[], int off, int len) throws IOException
    {
        if (isEnabled) {
            for (TerminalAgent.Audit auditor : auditors)
            {
                for (int i = 0, j = off; i < len; i++, j++)
                {
                    auditor.standardInput(
                            b[ j ]);
                }
            }
        }

        out.write(b, off, len);
    }


    //------------------------------------------------------------------------
    public void setEnabled(boolean isEnabled)
    {
        this.isEnabled = isEnabled;
    }
}
