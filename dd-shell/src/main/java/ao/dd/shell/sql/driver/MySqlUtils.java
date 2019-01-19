package ao.dd.shell.sql.driver;

import ao.dd.shell.sql.SqlConsole;
import ao.dd.shell.sql.Table;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: aostrovsky
 * Date: 20-Jul-2009
 * Time: 7:43:47 AM
 */
public class MySqlUtils
{
    //--------------------------------------------------------------------
    private MySqlUtils() {}


    //--------------------------------------------------------------------
    public static String fillSqlVals(
            String sql, String... vals)
    {
//        sql = sql.replaceAll("\\s+", " ");
        for (String val : vals)
        {
            sql = sql.replaceFirst(
            		"\\?", escapeMySql(val));
        }
        return sql;
    }

    private static String escapeMySql(String sql)
    {
        return "'" + sql.replaceAll(
                "(\\x00|\\n|\\r|\\\\|'|\"|\\x1a)",
                "\\$1") + "'";
    }


    //--------------------------------------------------------------------
    public static String allColumns(
            SqlConsole sql, String forTable)
    {
        return allColumns(bitColumns(
                 sql, forTable));
    }

    public static String allColumns(
            Map<String, Boolean> bitColumns)
    {
        StringBuilder str = new StringBuilder();

        int index = 0;
        for (Map.Entry<String, Boolean> column :
                bitColumns.entrySet())
        {
            if (column.getValue())
            {
                String name = column.getKey();
                str.append("if(")
                        .append(name)
                   .append(",'1','0') as '")
                        .append(name)
                   .append("'");
            }
            else
            {
                str.append(column.getKey());
            }

            if (index++ != bitColumns.size() - 1)
            {
                str.append(", ");
            }
        }

        return str.toString();
    }


    //--------------------------------------------------------------------
    public static Map<String, Boolean> bitColumns(
            SqlConsole sql, String forTable)
    {
        Map<String, Boolean> bitColumns =
                new LinkedHashMap<String, Boolean>();

        // todo: sql escape 'forTable'
        Table fields = sql.query("DESCRIBE " + forTable + ";");

        for (String[] row : fields)
        {
            String  field = row[ fields.column("Field") ];
            String  type  = row[ fields.column("Type" ) ];
            boolean isBit = type.matches("bit\\(\\d+\\)");

            bitColumns.put(field, isBit);
        }

        return bitColumns;
    }
}
