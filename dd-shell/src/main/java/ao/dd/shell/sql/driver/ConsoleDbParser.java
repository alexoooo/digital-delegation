package ao.dd.shell.sql.driver;

import java.io.Serializable;

import javolution.text.Text;

/**
 * User: aostrovsky
 * Date: 8-Oct-2009
 * Time: 7:48:02 AM
 */
public interface ConsoleDbParser
{
    //--------------------------------------------------------------------
    public String[] columnNames();

    public String[] row(int row);

    public int      rowCount();


    //--------------------------------------------------------------------
    public static interface Factory extends Serializable {
        public ConsoleDbParser newInstance(Text consoleData);
        public String          prompt();
    }
}
