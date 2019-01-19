package ao.dd.desktop.control.mouse;

import java.awt.event.InputEvent;

/**
* User: 188952
* Date: Apr 18, 2010
* Time: 12:49:47 AM
*/
public enum MouseButton
{
    //-------------------------------------------------------------------------
//    BUTTON_1(InputEvent.BUTTON1_DOWN_MASK),
//    BUTTON_2(InputEvent.BUTTON2_DOWN_MASK),
//    BUTTON_3(InputEvent.BUTTON3_DOWN_MASK);

    BUTTON_1(InputEvent.BUTTON1_MASK),
    BUTTON_2(InputEvent.BUTTON2_MASK),
    BUTTON_3(InputEvent.BUTTON3_MASK);

    
    //-------------------------------------------------------------------------
    private final int inputEvent;


    //-------------------------------------------------------------------------
    private MouseButton(
            int mouseInputEvent)
    {
        inputEvent = mouseInputEvent;
    }


    //-------------------------------------------------------------------------
    public int inputEvent()
    {
        return inputEvent;
    }
}
