package ao.dd.shell.impl.terminal.audit;

import ao.dd.shell.def.TerminalAgent;

/**
 * User: AO
 * Date: 10/26/10
 * Time: 8:58 PM
 */
public class ConsoleTerminalAudit
        implements TerminalAgent.Audit
{
    //------------------------------------------------------------------------
//    private final StringBuffer input  = new StringBuffer();
//    private final StringBuffer output = new StringBuffer();
//    private final StringBuffer error  = new StringBuffer();


    //------------------------------------------------------------------------
    @Override
    public void standardInput(int byteValue)
    {
        System.out.print((char) byteValue);
//        input.append((char) byteValue);
    }

    @Override
    public void standardOutput(int byteValue)
    {
        System.out.print((char) byteValue);
//        output.append((char) byteValue);
    }

    @Override
    public void standardError(int byteValue)
    {
        System.out.print((char) byteValue);
//        error.append((char) byteValue);
    }
}
