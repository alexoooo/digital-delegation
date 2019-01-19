package ao.dd.desktop.dash.capture.control;

import ao.dd.desktop.dash.capture.Clipper;
import ao.dd.desktop.model.display.Displays;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;

/**
 * User: Mable
 * Date: Aug 11, 2010
 * Time: 2:48:39 PM
 */
public class ScreenClipperFlow
        extends JPanel
{
    //------------------------------------------------------------------------
//    private static final Logger LOG =
//            LoggerFactory.getLogger( ScreenClipperFlow.class );


    //------------------------------------------------------------------------
    private final Clipper clipper;
//    private final Document speedText;
    private final LongTextField speedText;
    private final JButton       flowControl;

    private long    millisPerFrame = 100;
    private boolean nowPlaying;


    //------------------------------------------------------------------------
    public ScreenClipperFlow(Clipper clipper)
    {
        this.clipper = clipper;

        flowControl = new JButton();
        flowControl.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                changeControl(! nowPlaying);
            }});

        speedText = new LongTextField( millisPerFrame );
        speedText.getDocument().addDocumentListener(
                new DocumentListener() {
            @Override public void insertUpdate(
                    DocumentEvent e) {  changed();  }
            @Override public void removeUpdate(
                    DocumentEvent e) {  changed();  }
            @Override public void changedUpdate(
                    DocumentEvent e) {  changed();  }
            private void changed()
            {
                millisPerFrame = speedText.value();
            }
        });

        add( flowControl );
        add( new JLabel("   | ") );
        add( speedText );
        add( new JLabel(" milliseconds per frame") );

        changeControl( true );

        flowControl.setRequestFocusEnabled( true );
    }

    private void startPlaying()
    {
        final java.util.Timer t = new java.util.Timer(
                "Screen Clipper Flow", false);
        t.scheduleAtFixedRate(new TimerTask() {
            private long prev = -1;
            @Override public void run() {
                if (! nowPlaying) {
                    t.cancel();
                    return;
                }

                if (prev == -1 ||
                    (System.currentTimeMillis() - prev) > millisPerFrame)
                {
                    clipper.setSubject(
                            Displays.mainScreen().asArea().capture() );

                    prev = System.currentTimeMillis();
                }
            }}, 100, 10);
    }


    //------------------------------------------------------------------------
    private void changeControl(boolean playing)
    {
        if (playing) {
            flowControl.setText("Pause");
        } else {
            flowControl.setText("Play");
        }

        if (playing == nowPlaying) {
            return;
        }

        nowPlaying = playing;
        if (nowPlaying) {
            startPlaying();
        }
    }
}
