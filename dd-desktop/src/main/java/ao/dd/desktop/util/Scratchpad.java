package ao.dd.desktop.util;

import ao.dd.desktop.model.pixel.Pixel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: aostrovsky
 * Date: 26-May-2009
 * Time: 4:20:04 PM
 */
public class Scratchpad
{
    //--------------------------------------------------------------------
    private Scratchpad() {}


    //--------------------------------------------------------------------
    private static JFrame frame;

    static
    {
        frame = new JFrame("Scratchpad");

        frame.setVisible(true);
    }



    //--------------------------------------------------------------------
    public static void display(Image img)
    {
        Container c = frame.getContentPane();

        c.removeAll();
        c.add( new JLabel(new ImageIcon(
                img.getScaledInstance(500, 500, Image.SCALE_FAST))) );
        frame.pack();
    }

    public static void display(
            BufferedImage on,
            Pixel topLeftOfCandidate,
            int           width,
            int           height)
    {
        Rectangle toDisplay =
                topLeftOfCandidate.toRectangle(
                        width, height);

        display(on.getSubimage(
                toDisplay.x    , toDisplay.y     ,
                toDisplay.width, toDisplay.height));
    }
}
