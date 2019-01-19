package ao.dd.shell.impl.transfer.bulk;

import ao.dd.shell.def.TransferAgent;
import ao.util.io.AoFiles;
import ao.util.io.Dirs;

/**
 * User: aostrovsky
 * Date: 23-Jun-2009
 * Time: 1:55:02 PM
 */
public class PendingTransfer
{
    //--------------------------------------------------------------------
    private final String FROM;
    private final String TO;


    //--------------------------------------------------------------------
    public PendingTransfer(
            String from, String to)
    {
        FROM = from;
        TO   = to;
    }


    //--------------------------------------------------------------------
    public String from()
    {
        return FROM;
    }

    public String to()
    {
        return TO;
    }

    
    //--------------------------------------------------------------------
    public void download(TransferAgent bot)
    {
        Dirs.pathTo(TO);
        bot.download(FROM, TO);
    }


    //--------------------------------------------------------------------
    public void upload(TransferAgent bot)
    {
        bot.makeDirs(AoFiles.path(TO));
        bot.upload(FROM, TO);
    }


    //--------------------------------------------------------------------
    @Override public String toString()
    {
        return FROM + "  ->  " + TO;
    }
}
