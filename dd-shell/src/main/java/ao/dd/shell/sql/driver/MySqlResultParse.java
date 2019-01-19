package ao.dd.shell.sql.driver;

import ao.util.data.primitive.IntList;
import javolution.text.CharSet;
import javolution.text.Text;
import javolution.util.FastTable;
import org.apache.log4j.Logger;

/**
 * User: aostrovsky
 * Date: 20-Jul-2009
 * Time: 7:44:08 AM
 */
@SuppressWarnings("serial")
public class MySqlResultParse implements ConsoleDbParser
{
    //--------------------------------------------------------------------
    private static final Logger  LOG       =
            Logger.getLogger(MySqlResultParse.class);

    private final static CharSet NEW_LINES =
            CharSet.valueOf("\n\r".toCharArray());


    //--------------------------------------------------------------------
    public static Factory FACTORY = new Factory() {
        public ConsoleDbParser newInstance(Text consoleData) {
            return new MySqlResultParse(consoleData);
        }
        public String prompt() {
            return "mysql> ";
        }
    };


    //--------------------------------------------------------------------
    private final String           columnNames[];
    private final Text             consoleData;
    private final FastTable<int[]> tableLines;
    private final int[]            columnBreaks;


    //--------------------------------------------------------------------
    public MySqlResultParse(Text results)
    {
        consoleData = results;
        tableLines  = tableLines(results);
        if (tableLines.size() < 2)
        {
            boolean gotError = false;
            for (int[] lineBounds : lines(results)) {
                Text line = results.subtext(
                        lineBounds[0], lineBounds[1]);
                if (line.indexOf("ERROR") > -1) {
                    LOG.warn(line);
                    gotError = true;
                }
            }

            if (gotError)
            {
                columnNames  = null;
                columnBreaks = null;
            }
            else
            {
                columnNames  = new String[0];
                columnBreaks = new int[0];
            }
        }
        else
        {
            columnBreaks = parseColumnBreaks(line(1));
            columnNames  = parseRow(line(1), columnBreaks);
        }
    }


    //--------------------------------------------------------------------
    private Text line(int i)
    {
        int[] fromTo = tableLines.get(i);
        return consoleData.subtext(
                fromTo[0], fromTo[1]);
    }


    //--------------------------------------------------------------------
    private static String[] parseRow(
            Text row, int columnBreaks[])
    {
        String values[] = new String[ columnBreaks.length - 1 ];

        for (int i = 0; i < columnBreaks.length - 1; i++)
        {
            values[ i ] = row.subtext(
                                columnBreaks[i] + 1,
                                columnBreaks[i + 1]
                          ).trim().toString();
        }

        return values;
    }

    private static int[] parseColumnBreaks(Text header)
    {
        IntList breaks = new IntList();
        for (int offset = header.indexOf('|', 0);
                 offset != -1;
                 offset = header.indexOf('|', offset + 1))
        {
            breaks.add(offset);
        }
        return breaks.toIntArray();
    }


    //--------------------------------------------------------------------
    private static FastTable<int[]> tableLines(Text results)
    {
        FastTable<int[]> tableLines = new FastTable<int[]>();

        int titleLineCount = 0;
        for (int[] lineBounds : lines(results)) {
            Text line = results.subtext(lineBounds[0], lineBounds[1]);

            if (line.toString().matches("^(\\+-+)+\\+$")) {
                titleLineCount++;
            }

            if (titleLineCount == 0) continue;
            tableLines.add( lineBounds );
            if (titleLineCount == 3) break;
        }

        return tableLines;
    }

    private static FastTable<int[]> lines(Text results)
    {
        FastTable<int[]> lines    = new FastTable<int[]>();
        int[]            nextLine = new int[]{-1, -1};
        while ((nextLine = nextLine(
                results, nextLine[1] + 1)) != null) {
            if (nextLine[0] == nextLine[1]) continue;
            lines.add( nextLine );
        }
        return lines;
    }

    private static int[] nextLine(Text in, int fromInclusive) {
        if (in.length() < fromInclusive) {
            return null;
        }

        int from = fromInclusive;
        while (in.length() < from &&
                NEW_LINES.contains(in.charAt(from))) {
            from++;
        }

        int nextNewLine = in.indexOfAny(NEW_LINES, from);
        if (nextNewLine == -1) {
            nextNewLine = in.length();
        }

        return new int[]{
                from, nextNewLine };
    }



    //--------------------------------------------------------------------
    public String[] columnNames()
    {
        return columnNames;
    }


    //--------------------------------------------------------------------
    public String[] row(int row) {
        return parseRow(line(dataIndex(row)), columnBreaks);
    }

    public int rowCount() {
        return tableLines.size() < 4
               ? 0 : tableLines.size() - 4;
    }

    private static int dataIndex(int row) {
        return row + 3;
    }
}
