package ao.dd.desktop.control.keyboard;

import ao.dd.desktop.util.DesktopUtils;
import ao.util.time.Sched;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * User: aostrovsky
 * Date: 26-May-2009
 * Time: 10:10:56 AM
 */
//@SuppressWarnings("serial")
public class Keyboard
{
    //-------------------------------------------------------------------------
    private Keyboard() {}


    //-------------------------------------------------------------------------
    private static final Logger LOG =
            LoggerFactory.getLogger( Keyboard.class );

    private static final Robot BOT = DesktopUtils.newRobot();


    //-------------------------------------------------------------------------
    private static long   actionAutoDelay = 250;
    private static long   typingAutoDelay = 10;
//    private static double autoDelayNoise  = 0.1;


    //-------------------------------------------------------------------------
    public synchronized static long setActionAutoDelay(long autoDelayMillis)
    {
        long prev = actionAutoDelay;
        actionAutoDelay = autoDelayMillis;
        return prev;
    }

    public synchronized static long setTypingAutoDelay(long autoDelayMillis)
    {
        long prev = typingAutoDelay;
        typingAutoDelay = autoDelayMillis;
        return prev;
    }

    private static void sleepAction()
    {
        Sched.sleep( actionAutoDelay );
    }

    private static void sleepTyping()
    {
        Sched.sleep( typingAutoDelay );
    }


    //-------------------------------------------------------------------------
    public synchronized static boolean capsLocked()
    {
        return Toolkit.getDefaultToolkit()
                      .getLockingKeyState(
                              KeyEvent.VK_CAPS_LOCK);
    }

    public synchronized static boolean lockCaps()
    {
        if (capsLocked()) return false;
        sleepAction();

        type(KeyEvent.VK_CAPS_LOCK);
        return true;
    }

    public synchronized static boolean unlockCaps()
    {
        if (! capsLocked()) return false;
        sleepAction();

        type(KeyEvent.VK_CAPS_LOCK);
        return true;
    }


    //--------------------------------------------------------------------
    public synchronized static void type(String text)
    {
        sleepAction();
        for (int i = 0; i < text.length(); i++)
        {
            type(text.charAt( i ));
        }
    }


    //--------------------------------------------------------------------
    public synchronized static void type(char character)
    {
        int     event;
        boolean shiftNeeded = false;

        if (Character.isSpaceChar(character))
        {
            event = KeyEvent.VK_SPACE;
        }
        else if (Character.isLetter(character))
        {
            event       = ALPHANUM_KEY_EVENTS.get(
                    Character.toLowerCase(character));
            shiftNeeded =
                    Character.isUpperCase(character) ^ capsLocked();
        }
        else if (Character.isDigit(character))
        {
            event = ALPHANUM_KEY_EVENTS.get(character);
        }
        else if (BASE_SYM_KEY_EVENTS.containsKey(character))
        {
            event = BASE_SYM_KEY_EVENTS.get(character);
        }
        else if (SHIFT_SYM_KEY_EVENTS.containsKey(character))
        {
            event       = SHIFT_SYM_KEY_EVENTS.get(character);
            shiftNeeded = true;
        }
        else
        {
//            bot.keyPress  (KeyEvent.SHIFT_MASK);
//            // enter with numpad?
//            bot.keyRelease(KeyEvent.SHIFT_MASK);
//            return;
            throw new Error("untypable character: " + character +
                            "(" + (int) character + ")");
        }

        type(shiftNeeded, event);
    }


    //--------------------------------------------------------------------
//    public static void type(
//            int...  keyEvents)
//    {
//        type(true, keyEvents);
//    }
    public synchronized static void type(
//            boolean releaseInOrder,
            int...  keyEvents)
    {
        for (int keyCode : keyEvents)
        {
            sleepTyping();
            try {
                BOT.keyPress(keyCode);
            } catch (Throwable e) {
//                throw Throwables.propagate( e );
                throw new Error("Can't press: " + keyCode +
                        " '" + ((char) keyCode) + "'");
            }
        }

//        if (releaseInOrder) {
            for (int i = keyEvents.length - 1; i >= 0; i--)
            {
                sleepTyping();
                BOT.keyRelease(keyEvents[i]);
            }
//        } else {
//            for (int keyCode : keyEvents)
//            {
//                bot.keyRelease(keyCode);
//            }
//        }
    }

    private static void type(
            boolean shiftNeeded, int keyEvent)
    {
        if (shiftNeeded)
        {
            type(KeyEvent.VK_SHIFT, keyEvent);
        }
        else
        {
            type(keyEvent);
        }
    }


    //--------------------------------------------------------------------
    private static final Map<Character, Integer> ALPHANUM_KEY_EVENTS =
            new HashMap<Character, Integer>(){{
                put('a', KeyEvent.VK_A);
                put('b', KeyEvent.VK_B);
                put('c', KeyEvent.VK_C);
                put('d', KeyEvent.VK_D);
                put('e', KeyEvent.VK_E);
                put('f', KeyEvent.VK_F);
                put('g', KeyEvent.VK_G);
                put('h', KeyEvent.VK_H);
                put('i', KeyEvent.VK_I);
                put('j', KeyEvent.VK_J);
                put('k', KeyEvent.VK_K);
                put('l', KeyEvent.VK_L);
                put('m', KeyEvent.VK_M);
                put('n', KeyEvent.VK_N);
                put('o', KeyEvent.VK_O);
                put('p', KeyEvent.VK_P);
                put('q', KeyEvent.VK_Q);
                put('r', KeyEvent.VK_R);
                put('s', KeyEvent.VK_S);
                put('t', KeyEvent.VK_T);
                put('u', KeyEvent.VK_U);
                put('v', KeyEvent.VK_V);
                put('w', KeyEvent.VK_W);
                put('x', KeyEvent.VK_X);
                put('y', KeyEvent.VK_Y);
                put('z', KeyEvent.VK_Z);

                put('0', KeyEvent.VK_0);
                put('1', KeyEvent.VK_1);
                put('2', KeyEvent.VK_2);
                put('3', KeyEvent.VK_3);
                put('4', KeyEvent.VK_4);
                put('5', KeyEvent.VK_5);
                put('6', KeyEvent.VK_6);
                put('7', KeyEvent.VK_7);
                put('8', KeyEvent.VK_8);
                put('9', KeyEvent.VK_9);
            }};

    private static final Map<Character, Integer> BASE_SYM_KEY_EVENTS =
            new HashMap<Character, Integer>(){{
                put('`' , KeyEvent.VK_BACK_QUOTE);
                put('-' , KeyEvent.VK_MINUS);
                put('=' , KeyEvent.VK_EQUALS);
                put('[' , KeyEvent.VK_BRACELEFT);
                put(']' , KeyEvent.VK_BRACERIGHT);
                put('\\', KeyEvent.VK_BACK_SLASH);
                put(';' , KeyEvent.VK_SEMICOLON);
                put('\'', KeyEvent.VK_QUOTE);
                put(',' , KeyEvent.VK_COMMA);
                put('.' , KeyEvent.VK_PERIOD);
                put('/' , KeyEvent.VK_SLASH);
            }};

    private static final Map<Character, Integer> SHIFT_SYM_KEY_EVENTS =
            new HashMap<Character, Integer>(){{
                put('~' , KeyEvent.VK_BACK_QUOTE);
                put('!' , KeyEvent.VK_1);
                put('@' , KeyEvent.VK_2);
                put('#' , KeyEvent.VK_3);
                put('$' , KeyEvent.VK_4);
                put('%' , KeyEvent.VK_5);
                put('^' , KeyEvent.VK_6);
                put('&' , KeyEvent.VK_7);
                put('*' , KeyEvent.VK_8);
                put('(' , KeyEvent.VK_9);
                put(')' , KeyEvent.VK_0);
                put('_' , KeyEvent.VK_MINUS);
                put('+' , KeyEvent.VK_EQUALS);
                put('{' , KeyEvent.VK_BRACELEFT);
                put('}' , KeyEvent.VK_BRACERIGHT);
                put('|' , KeyEvent.VK_BACK_SLASH);
                put(':' , KeyEvent.VK_SEMICOLON);
                put('"' , KeyEvent.VK_QUOTE);
                put('<' , KeyEvent.VK_COMMA);
                put('>' , KeyEvent.VK_PERIOD);
                put('?' , KeyEvent.VK_SLASH);
            }};
}
