package ao.dd.shell.impl.terminal.stat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Mable
 * Date: May 17, 2010
 * Time: 10:29:54 PM
 */
public class PsParser
{
    //--------------------------------------------------------------------
    private static final Pattern PS_PAT = Pattern.compile(
            "\\s*(\\d+)\\s+(\\d+)\\s+([0-9.]+)\\s+(\\d+)\\s+(.+)");


    //--------------------------------------------------------------------
    public PsParser() {}


    //--------------------------------------------------------------------
    public PsRecord parse(
            String line)
    {
        Matcher match = PS_PAT.matcher(line);
        if (! match.matches()) return null;

        return new PsRecord(
                match.group(1),
                match.group(2),
                Double.parseDouble(match.group(3)),
                Long.parseLong(match.group(4)),
                match.group(5)
        );
    }
}
