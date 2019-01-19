package ao.dd.shell.impl.terminal.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: aostrovsky
 * Date: 9-Feb-2010
 * Time: 10:47:37 AM
 *
 * -rw-r--r--   1 aostrovsky staff   24478 Jul  7 22:03 SecondaryToCft.jar
 * drwxr-xr-x   2 aostrovsky staff     512 Jul  6 23:24 lib
 */
public class LsParser
{
    //--------------------------------------------------------------------
    private static final Pattern LS_PAT = Pattern.compile(
            "(\\S+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)" +
                    "\\s+(\\S+)\\s+(\\d+)\\s+(\\S+)\\s+(\\S+)");


    //--------------------------------------------------------------------
    public LsParser() {}


    //--------------------------------------------------------------------
    public List<LsRecord> parse(String lsMinusL) {
        if (lsMinusL.contains("No such file or directory")) {
            return Collections.emptyList();
        }

        List<LsRecord> files = new ArrayList<LsRecord>();
        for (String line : lsMinusL.split("\n")) {
            Matcher matcher = LS_PAT.matcher( line.trim() );
            if (! matcher.matches()) continue;

            files.add(new LsRecord(
                    matcher.group(1), matcher.group(2),
                    matcher.group(3), matcher.group(4),
                    matcher.group(5), matcher.group(6),
                    matcher.group(7), matcher.group(8),
                    matcher.group(9)));
        }
        return files;
    }
}
