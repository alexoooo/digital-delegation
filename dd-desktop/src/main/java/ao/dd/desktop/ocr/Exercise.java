package ao.dd.desktop.ocr;

import ao.dd.desktop.control.QuickAction;
import ao.dd.desktop.control.keyboard.Keyboard;
import ao.dd.desktop.model.display.Displays;
import ao.dd.desktop.model.image.RgbGrid;
import ao.dd.desktop.util.Pictures;
import ao.dd.desktop.vision.locate.Locator;
import ao.dd.desktop.vision.locate.SimpleLocator;
import ao.util.io.AoFiles;
import ao.util.math.rand.Rand;
import ao.util.pass.DataSink;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;

/**
 * User: Eugene
 * Date: May 12, 2010
 * Time: 6:32:13 PM
 */
public class Exercise
{
    //------------------------------------------------------------------------
    private static final Logger LOG = LoggerFactory.getLogger(
            Exercise.class);

    private static final String WORK_DIR = "work/ocr3";

    private static final java.util.List<String> FONTS = Arrays.asList(
            "Times New Roman", "Calibri", "Courier New",
            "Georgia", "Cambria", "Arial",
            "Verdana");

    private static final int standardWidth  = 13;
    private static final int standardHeight = 18;


    //-------------------------------------------------------------------------
    public static void main(String[] args)
    {
        Rand.randomize();

        Locator locator = new SimpleLocator();

        Rectangle notePadWindow =
                new Rectangle( 347, 180, 920 - 347, 210 - 180);
        LOG.info("notepad Rectangle: " + notePadWindow.toString());

        String allChars =
                "1234567890" +
                "abcdefghijklmnopqrstuvwxyz" +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "   ";
//                "!?   \".()$#@%&*+-=<>/^\\|_:;'~`"; // {}[],
//                "!?   \".()$#@%&*+-=<>/^\\{}[]|_:;'~`,";

        LearnUsingWordPad wordPad = new LearnUsingWordPad();
        wordPad.open( true );

        String  font       = Rand.fromList( FONTS );
        boolean bold       = false,
                italic     = false,
                underlined = false;

        wordPad.changeFont ( font );

        Rectangle lineArea = new Rectangle() ;

//        boolean bold = true, italic = true, underlined = true;
//        wordPad.changeFont ( font   = Rand.fromList( FONTS ) );
//        wordPad.bold       ( bold       = Rand.nextBoolean() );
//        wordPad.italic     ( italic     = Rand.nextBoolean() );
//        wordPad.underlined ( underlined = Rand.nextBoolean() );

//        java.util.List<Pair<String, Rectangle>>
//                letters = Lists.newArrayList();

        RgbGrid letterDiff_a = Displays.mainScreen().view( notePadWindow );

        Keyboard.setTypingAutoDelay(7);


        long  runId = Math.abs( Rand.nextInt() );
        long lineId = 0;

        Map<Integer, Character> offsetToCharacter;
        offsetToCharacter = Maps.newTreeMap();

        for (int i = 1 ; i < 1000000; i++)
        {
            if ( i % 50 == 0)
            {
//                Keyboard.type(KeyEvent.VK_ENTER);
//                Keyboard.type(KeyEvent.VK_ENTER);

                RgbGrid textCapture =
                        Displays.mainScreen().view( notePadWindow );

                String lineName =
                        runId + "-" + lineId + "-" +
                        font  + "-" +
                        (bold       ? "b" : "_") +
                        (italic     ? "i" : "_") +
                        (underlined ? "u" : "_");

//                lineArea.y += (lineArea.height - standardHeight ) / 2;
                lineArea.height = standardHeight;

                Pictures.save(
                            textCapture.sub(
                                    lineArea
                            ).toBufferedImage(),
                            WORK_DIR+"/lines",
                            lineName,
                            false);

//                for (Pair<String, Rectangle> letter : letters)
//                {
//
//                    Pictures.save(
//                            textCapture.sub(
//                                    letter.second()
//                            ).toBufferedImage(),
//                            WORK_DIR+"/letters",
//                            letter.first(),
//                            false);
//                }

//                letters.clear();

                QuickAction.clearText();

                wordPad.changeFont ( font   = Rand.fromList( FONTS ) );

                wordPad.bold       ( bold       = Rand.nextBoolean() );
                wordPad.italic     ( italic     = Rand.nextBoolean() );
                wordPad.underlined ( underlined = Rand.nextBoolean() );

                writeLineInfo(runId, lineId, offsetToCharacter);
                lineId++;
                offsetToCharacter = Maps.newTreeMap();
            }

            Character nextChar = allChars.charAt(
                    Rand.nextInt( allChars.length() ));

            Keyboard.type(nextChar);

            RgbGrid letterDiff_b = Displays.mainScreen().view( notePadWindow );
            Rectangle diff = locator.difference(
                    letterDiff_a,  letterDiff_b);

            Rectangle normalDiff =
                    (diff == null ||
                     diff.width > standardWidth ||
                     diff.height > standardHeight)
                    ? null
                    : new Rectangle(
                            Math.max(0, (int) Math.round(
                                          (double) diff.x  - (
                                          (double) standardWidth  / 2
                                        - (double) diff   .width  / 2))),
                            Math.max(0, (int) Math.round(
                                          (double) diff.y + 3 - (
                                          (double) standardHeight / 2
                                        - (double) diff   .height / 2 ))),
                            standardWidth, standardHeight - 2);

            if (normalDiff       != null &&
                normalDiff.width  > 0 &&
                normalDiff.height > 0)
            {
//                if (i % 55 == 0)
//                {
//                    normalDiff = new Rectangle(
//                            Math.max(0, (int) Math.round(
//                                          (double) diff.x  - (
//                                          (double) standardWidth  / 2
//                                        - (double) diff   .width  / 2))),
//                            Math.max(0, (int) Math.round(
//                                          (double) diff.y + 1 - (
//                                          (double) standardHeight / 2
//                                        - (double) diff   .height / 2))),
//                            standardWidth - 1, standardHeight - 2);
//                }
//                else
                {
//                    LOG.debug("Rectangle: "   +       diff.toString()     +
//                          " CHAR: "       +       nextChar.toString() +
//                          " ASCII #: "    + (int) nextChar            +
//                          " FONT: "       +       font                +
//                          " BOLD: "       +       bold                +
//                          " ITALIC: "     +       italic              +
//                          " UNDERLINED: " +       underlined          );
//
//                    String saveAsFileName =
//                            (int) nextChar       +"_"+
//                            (int) nextChar      +"_"+
//                            font                +"_"+
//                            bold                +"_"+
//                            italic              +"_"+
//                            underlined
//                            /*+ "_" +*/ i + "_" + Rand.nextInt();
//
//                    letters.add(Tuples.create(
//                            saveAsFileName, normalDiff));

                    offsetToCharacter.put(
                            normalDiff.x,
                            nextChar);

                    if (lineArea.height == 0)
                    {
                        lineArea.x = normalDiff.x;
                        lineArea.y = normalDiff.y;
                        lineArea.width = notePadWindow.width
                                        - normalDiff.x;
                        lineArea.height = normalDiff.height;
                    }
                    else if (lineArea.y > normalDiff.y)
                    {
                        lineArea.height =
                                lineArea.y -
                                normalDiff.y   +
                                lineArea.height;

                        lineArea.y = normalDiff.y;
                    }
                    else if (normalDiff.y + normalDiff.height >
                             lineArea.y + lineArea.height)
                    {
                        lineArea.height = normalDiff.y + normalDiff.height;
                    }
                }
            }
            letterDiff_a =  letterDiff_b;
        }
    }


    //------------------------------------------------------------------------
    private static void writeLineInfo(
            final long                    runId,
            final long                    lineId,
            final Map<Integer, Character> offsetToCharacter)
    {
        File fileName = new File(WORK_DIR,
                runId          + "-" +
                standardWidth  + ".csv");

        AoFiles.write(fileName, true, new DataSink<PrintWriter>() {
            @Override public void process(
                    PrintWriter out) throws Exception
            {
                StringBuilder str = new StringBuilder();

                str.append( lineId );

                for (Map.Entry<Integer, Character>
                        e : offsetToCharacter.entrySet())
                {
                    str.append( "\t"          )
                       .append( e.getKey()   )
                       .append( "\t"          )
                       .append( e.getValue() );
                }

                out.println( str.toString() );
            }});
    }
}
