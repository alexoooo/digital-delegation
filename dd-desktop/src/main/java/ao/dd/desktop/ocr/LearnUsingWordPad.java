package ao.dd.desktop.ocr;

import ao.dd.desktop.control.QuickAction;
import ao.dd.desktop.control.keyboard.Keyboard;
import ao.dd.desktop.model.display.Displays;
import ao.dd.desktop.ocr.win7resources.ForNotepad;
import ao.util.time.Sched;

import java.awt.event.KeyEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Eugene
 * Date: May 12, 2010
 * Time: 6:17:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class LearnUsingWordPad
{
    //-------------------------------------------------------------------------
    private String learnString;
    //    private Font   learnFont  ;


    //-------------------------------------------------------------------------
    public LearnUsingWordPad()
    {
        this(null);
    }

    public LearnUsingWordPad(String textToLearn)
    {
        learnString = textToLearn;
    }

    //-------------------------------------------------------------------------
    public void open(
            boolean setParagraphClose)
    {
        QuickAction.find( "start.png", ForNotepad.class, Displays.mainScreen() )
                .center().click();

        Sched.sleep(10);
        Keyboard.type( "wordPad" );
        Keyboard.type( KeyEvent.VK_ENTER );
        Sched.sleep(1000);

        if (setParagraphClose)
        {
            Keyboard.type( KeyEvent.VK_ALT );
            Keyboard.type( KeyEvent.VK_H );
            Keyboard.type( KeyEvent.VK_P );
            Keyboard.type( KeyEvent.VK_G );
            Sched.sleep( 10 );
            Keyboard.type( KeyEvent.VK_TAB );
            Keyboard.type( KeyEvent.VK_TAB );
            Keyboard.type( KeyEvent.VK_TAB );
            Keyboard.type( KeyEvent.VK_DOWN );
            Keyboard.type( KeyEvent.VK_DOWN );
            Keyboard.type( KeyEvent.VK_TAB );
            Keyboard.type( KeyEvent.VK_TAB );
            Keyboard.type( KeyEvent.VK_TAB );
            Keyboard.type( KeyEvent.VK_TAB );
            Keyboard.type( KeyEvent.VK_SPACE );
        }

        Displays.mainScreen().asArea().center().move();
    }

    public void changeFont (String fontName)
    {
        Keyboard.type(KeyEvent.VK_ALT);
        Keyboard.type(KeyEvent.VK_H);
        Keyboard.type(KeyEvent.VK_F);
        Keyboard.type(KeyEvent.VK_1);
        Keyboard.type(KeyEvent.VK_1);

        Keyboard.type(KeyEvent.VK_X);

        Keyboard.type(KeyEvent.VK_HOME);

        for (int i = 0; i < 3; i++)
        {
            Keyboard.type(KeyEvent.VK_DELETE);
        }

        Keyboard.type(fontName);

        Keyboard.type(KeyEvent.VK_ENTER);
    }

    public void bold(boolean toggle)
    {
        if (toggle)
        {
            Keyboard.type( KeyEvent.VK_ALT );
            Keyboard.type( KeyEvent.VK_H   );
            Keyboard.type( KeyEvent.VK_B   );
        }
    }

    public void italic (boolean toggle)
    {
        if (toggle)
        {
            Keyboard.type( KeyEvent.VK_ALT );
            Keyboard.type( KeyEvent.VK_H   );
            Keyboard.type( KeyEvent.VK_I   );
        }
    }

    public void underlined (boolean toggle)
    {
        if (toggle)
        {
            Keyboard.type( KeyEvent.VK_ALT );
            Keyboard.type( KeyEvent.VK_H   );
            Keyboard.type( KeyEvent.VK_U   );
        }
    }
}
