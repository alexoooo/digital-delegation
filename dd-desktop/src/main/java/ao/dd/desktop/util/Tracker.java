package ao.dd.desktop.util;

import ao.dd.desktop.model.area.Area;
import ao.dd.desktop.model.display.Display;
import ao.dd.desktop.model.display.Displays;
import ao.dd.desktop.model.display.ImageDisplay;
import ao.dd.desktop.model.image.RgbGrid;
import ao.dd.desktop.vision.def.Feature;
import ao.dd.desktop.vision.def.Scene;
import ao.dd.desktop.vision.def.Vision;
import ao.dd.desktop.vision.feature.active_window.window_control.Xclose;
import ao.dd.desktop.vision.impl.FlatTreeVision;
import ao.util.data.tuple.Tuples;
import ao.util.data.tuple.TwoTuple;
import ao.util.io.AoFiles;
import ao.util.io.Dirs;
import ao.util.misc.AoThrowables;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.TimerTask;

/**
 * User: Mable
 * Date: Apr 7, 2010
 * Time: 11:08:32 PM
 */
public class Tracker
        extends JComponent
{
    //-------------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger( Tracker.class );

    private static final File captureDir = Dirs.get("caps");

    private static final long    pause      = 100;
    private static final long    colourSeed = 0;
//    private static       boolean autoSize   = true;


    //-------------------------------------------------------------------------
    public static void main(String[] args)
    {
        create().add(
                new Xclose()
//                new StartButton()
        );

//        vision.add( new StartButton());
//        vision.add( new Xclose() );
//        vision.add( new TaskBar());
//        vision.add( new Desktop());
//        vision.add( new SystemTray());
//        vision.add( new DateTimeNotification());
//        vision.add( new EdgeTaskBarBorder());
    }


    //-------------------------------------------------------------------------
    public static Tracker create()
    {
        try
        {
            return doCreate();
        }
        catch (Exception e)
        {
            throw AoThrowables.wrap( e );
        }
    }

    private static Tracker doCreate()
            throws InvocationTargetException, InterruptedException
    {
        final Tracker[] tracker = {null};
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                tracker[0] = buildAndDisplayGui();
            }});
        return tracker[ 0 ];
    }


    //-------------------------------------------------------------------------
    private static Tracker buildAndDisplayGui()
    {
        JFrame frame = new JFrame("Desktop Tracker");
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        TwoTuple<Tracker, JComponent> mainGui =
                buildMainGuiPanel();
                
        frame.getContentPane().add(
                 mainGui.second());

//        frame.pack();
        frame.setSize(400, 300);

        frame.setLocationRelativeTo( null );
        frame.setVisible( true );

        return mainGui.first();
    }


    //-------------------------------------------------------------------------
    private static TwoTuple<Tracker, JComponent> buildMainGuiPanel()
    {
        JPanel content = new JPanel();

        final Tracker tracker = new Tracker();
        tracker.startTimer();
        content.add( tracker );

        JPanel buttons = new JPanel();
        buttons.setLayout(
                new FlowLayout(FlowLayout.RIGHT));

        JButton captureButton = new JButton("Capture");
//        JButton autoSize      = new JButton(displayMethod());

        captureButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                String fileName = AoFiles.escapeName(
                        JOptionPane.showInputDialog(tracker,
                                "Please enter file name"));

                RgbGrid screen =
                        Displays.mainScreen().view();
                Pictures.save(
                        screen.toBufferedImage(), captureDir, fileName);
            }});

        buttons.add( captureButton );
        content.add( buttons );

        return Tuples.create(tracker, (JComponent) content);
    }

    
    //-------------------------------------------------------------------------
    private final Vision    vision;

    private volatile boolean beingComputed = false;


    //-------------------------------------------------------------------------
    public Tracker()
    {
        vision = new FlatTreeVision();
    }



    //-------------------------------------------------------------------------
    public void add(Feature... features)
    {
        vision.addAll( features );
    }

    public void add(Iterable<? extends Feature> features)
    {
        vision.addAll( features );
    }


    //-------------------------------------------------------------------------
    public void startTimer()
    {
//        System.out.println("starting timer");
        new java.util.Timer()
                .schedule(new TimerTask() {
                    @Override public void run() {
                        if (beingComputed) return;

//                        LOG.debug("scheduling repaint");
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override public void run() {
//                                LOG.debug("invoking repaint");
                                Tracker.this.repaint();
                            }});
                    }},
                    0, pause);
    }


    //-------------------------------------------------------------------------
    @Override
    public /*synchronized*/ void paint(Graphics g)
    {
        if (beingComputed) return;
        beingComputed = true;

//        if (nextScene == null) return;

        RgbGrid screenShot =
                Displays.mainScreen().asArea().capture();
        Display screen =
                new ImageDisplay( screenShot );
//                Displays.mainScreen();
        
        Area screenArea = screen.asArea();
//        BufferedImage screen =
//                screenArea.capture();
//        populateVision();

//        Scene scene = vision.see(  );


        LOG.debug( " DONE =======================================" );

        Scene scene = vision.see( screenArea );
        LOG.debug( "saw: \r" + scene );

//        Scene reScene = vision.see( screenArea );
//        LOG.debug( "reScene: \r" + reScene );

        Random rand = new Random( colourSeed );
        BufferedImage screenShotImage = screenShot.toBufferedImage();
        drawScene(screenShotImage.getGraphics(), scene, rand);

        g.drawImage(
                screenShotImage,
                0, 0,
                getWidth(), getHeight(),
                null);

        beingComputed = false;
    }

    private void drawScene(
            Graphics g, Scene scene, Random rand)
    {
        Rectangle area = scene.sighting().area().toRectangle();

        if (area == null) return;

//        g.setColor( Color.RED );
        g.setColor( nextColour(rand) );

        g.drawRect(
                area.x,     area.y,
                area.width, area.height);

        for (Scene kid : scene.kids())
        {
            drawScene(g, kid, rand);
        }
    }


    //-------------------------------------------------------------------------
    private Color nextColour(Random rand)
    {
        return new Color(
                rand.nextInt(255),
                rand.nextInt(255),
                rand.nextInt(255));
    }


    //-------------------------------------------------------------------------
    @Override
    public Dimension getPreferredSize()
    {
        // todo: this is a total hack, need to use proper layout manager
        return new Dimension(
                getParent().getSize().width,
                getParent().getSize().height - 50
        );
    }
}