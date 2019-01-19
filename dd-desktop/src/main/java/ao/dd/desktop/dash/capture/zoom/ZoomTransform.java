package ao.dd.desktop.dash.capture.zoom;

import ao.dd.desktop.model.image.RgbGrid;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Date: Aug 1, 2010
 * Time: 3:18:51 PM
 */
public class ZoomTransform
{
    //------------------------------------------------------------------------
    private final Rectangle backingArea;
    private final Rectangle displayArea;


    //------------------------------------------------------------------------
    public ZoomTransform(
            Rectangle backingArea,
            Rectangle displayArea)
    {
        this.backingArea = backingArea;
        this.displayArea = displayArea;
    }


    //------------------------------------------------------------------------
    public boolean validate(Dimension backingSize)
    {
        return !((backingArea.x < 0 ||
                  backingArea.y < 0 ||
                  backingArea.width  <= 0 ||
                  backingArea.height <= 0 ||
                 (backingArea.x + backingArea.width ) > backingSize.width  ||
                 (backingArea.y + backingArea.height) > backingSize.height));
    }


    //------------------------------------------------------------------------
    public void apply(
            RgbGrid backingImage, Rectangle selection, Graphics display)
    {
        if (! validate(new Dimension(
                backingImage.width(), backingImage.height())))
        {
            return;
        }

        if (selection == null /*||
                selection.width == 0 || selection.height == 0*/)
        {
            doApply(backingImage, display);
        }
        else
        {
            doApply(backingImage, selection, display);
        }
    }

    private void doApply(
            RgbGrid   backingImage,
            Rectangle selection,
            Graphics  display)
    {
        BufferedImage backingBuffer =
                backingImage.sub(
                        backingArea.x,
                        backingArea.y,
                        backingArea.width,
                        backingArea.height
                ).toBufferedImage();

        Graphics backingG = backingBuffer.getGraphics();

        backingG.setColor(new Color(0, 0, 255, 127));
        backingG.drawRect(
                selection.x - backingArea.x,
                selection.y - backingArea.y,
                selection.width, selection.height);

        backingG.setColor(new Color(0, 0, 255, 64));
        backingG.fillRect(
                selection.x - backingArea.x + 1,
                selection.y - backingArea.y + 1,
                selection.width - 1,
                selection.height - 1);

        backingG.dispose();

        drawOnDisplay(backingBuffer, display);
    }

    private void doApply(RgbGrid backingImage, Graphics display)
    {
        RgbGrid backingSubImage = backingImage.sub(
                backingArea.x, backingArea.y,
                backingArea.width, backingArea.height);

        BufferedImage img = backingSubImage.toBufferedImage();
        drawOnDisplay(img, display);
    }

    private void drawOnDisplay(Image img, Graphics display)
    {
        display.drawImage(
                img,
                displayArea.x, displayArea.y,
                displayArea.width,displayArea.height,
                null);
    }


    //------------------------------------------------------------------------
    public Rectangle backingArea()
    {
        return backingArea;
    }

    public Rectangle displayArea()
    {
        return displayArea;
    }

//    public double zoomLevel
    

    //------------------------------------------------------------------------
    @Override
    public String toString()
    {
        double widthRatio  = (double)
                backingArea.width  / displayArea.width;
        double heightRatio = (double)
                backingArea.height / displayArea.height;

//        String ratioTextSuffix = " %" + displayArea;
        String ratioTextSuffix = " %";
        String widthRatioText = String.valueOf((int)(widthRatio * 100));
        if (Math.abs(widthRatio - heightRatio) < 0.01)
        {
            return widthRatioText + ratioTextSuffix;
        }
        else
        {
            return widthRatioText + " x " +
                  (int)(heightRatio * 100) +
                    ratioTextSuffix;
        }
    }
}
