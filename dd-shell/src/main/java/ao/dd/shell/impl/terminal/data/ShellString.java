package ao.dd.shell.impl.terminal.data;

import javolution.text.Text;
import javolution.text.TextBuilder;

/**
 * User: aostrovsky
 * Date: 23-Jun-2009
 * Time: 2:41:34 PM
 */
public class ShellString
{
    //--------------------------------------------------------------------
//    private final StringBuilder raw;
    private final TextBuilder raw;


    //--------------------------------------------------------------------
    public ShellString()
    {
//        raw = new StringBuilder();
        raw = new TextBuilder();
    }


    //--------------------------------------------------------------------
    public void append(Text rawChunk)
    {
        for (int i = 0; i < rawChunk.length(); i++) {
            append( rawChunk.charAt(i) );
        }
    }


    //--------------------------------------------------------------------
    public void append(char c)
    {
        if (c == '\b' && raw.length() > 0) {
            raw.delete(raw.length() - 1, raw.length());
        } else {
            raw.append(c);
        }
    }


    //--------------------------------------------------------------------
    public boolean endMatches(byte[] sentinel)
    {
        for (int i = raw.length() - 1, j = sentinel.length - 1;
                 i >= 0 && j >= 0; i--, j--)
        {
            if (raw.charAt(i) != (char) (sentinel[j] & 0xFF))
            {
                return false;
            }
        }

        return true;
    }


    //--------------------------------------------------------------------
    public Text toText()
    {
        return raw.toText();
    }

    @Override public String toString()
    {
        return toText().toString();
    }

//    @Override public String toString()
//    {
////        StringBuilder clean = new StringBuilder();
//        TextBuilder clean = new TextBuilder();
//
//        for (int i = 0; i < raw.length(); i++)
//        {
//            char cursor = raw.charAt(i);
//            switch (cursor)
//            {
//                case '\b':
////                    clean.deleteCharAt( clean.length() - 1 );
//                    clean.delete();
//                    break;
//
//                default:
//                    clean.append(cursor);
//                    break;
//            }
//        }
//
//        return clean.toString();
//    }
}
