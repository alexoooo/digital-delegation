package ao.dd.desktop.dash.capture.control;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.util.regex.Pattern;

/**
 * Date: Aug 11, 2010
 * Time: 9:31:41 PM
 *
 * See: http://faq.javaranch.com/java/NumericTextField
 */
public class LongTextField extends JTextField
{
    //------------------------------------------------------------------------
    public LongTextField(long initialValue)
    {
        super(String.valueOf( initialValue ), 5);
    }


    //------------------------------------------------------------------------
    @Override
    protected Document createDefaultModel()
    {
        return new NumericDocument();
    }


    //------------------------------------------------------------------------
    public long value()
    {
        String clean = getText().replaceAll("\\D", "");

        try
        {
            return Long.parseLong( clean );
        }
        catch (Throwable ignored)
        {
            return 0;
        }
    }


    //------------------------------------------------------------------------
    private static class NumericDocument extends PlainDocument
    {
        // The regular expression to match
        //      input against (zero or more digits)
        private final static Pattern DIGITS =
                Pattern.compile("(\\d|,)*");

        @Override
        public void insertString(
                int offs, String str, AttributeSet a)
                    throws BadLocationException
        {
            // Only insert the text if it matches the regular expression
            if (str != null && DIGITS.matcher(str).matches())
            {
                super.insertString(offs, str, a);
            }
        }
    }
}
