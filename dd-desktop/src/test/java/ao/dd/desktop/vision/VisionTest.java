package ao.dd.desktop.vision;

import ao.dd.desktop.model.display.Displays;
import ao.dd.desktop.vision.def.Scene;
import ao.dd.desktop.vision.def.Vision;
import ao.dd.desktop.vision.feature.taskbar.border.win_xp.TaskBarBorderWinXP;
import ao.dd.desktop.vision.impl.FlatTreeVision;

/**
 * User: 188952
 * Date: Apr 2, 2010
 * Time: 9:48:53 PM
 */
public class VisionTest
{
    //-------------------------------------------------------------------------
    public static void main(String[] ignored)
    {
        Vision vision = new FlatTreeVision();

//        vision.add(new TaskBar());
        vision.add( new TaskBarBorderWinXP() );

        Scene  scene = vision.see(
                Displays.mainScreen().asArea());

        System.out.println(
                scene);
    }
}
