package ao.dd.desktop.util;

import ao.util.io.Dirs;
import com.google.common.base.Throwables;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: aostrovsky
 * Date: 26-May-2009
 * Time: 3:56:10 PM
 */
public class Pictures
{
    //-------------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger( Pictures.class );


    //-------------------------------------------------------------------------
    private Pictures() {}


    //-------------------------------------------------------------------------
    public static BufferedImage toBufferedImage(
            Image image)
    {
        return toBufferedImage(
                 image, hasAlpha(image));
    }

    public static BufferedImage toBufferedImage(
            Image image, boolean hasAlpha)
    {
        //Transparency.OPAQUE
        if (image instanceof BufferedImage)
        {
            return (BufferedImage) image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        if (image.getWidth (null) == -1 ||
            image.getHeight(null) == -1)
        {
            return null;
        }

        // Create a buffered image with a format
        //   that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        try
        {
            // Determine the type of transparency
            //  of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha)
            {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                    image.getWidth (null),
                    image.getHeight(null),
                    transparency);
        }
        catch (HeadlessException ignore)
        {
        } //No screen

        if (bimage == null)
        {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha)
            {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(
                    image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }


    //-------------------------------------------------------------------------
    public static BufferedImage toBufferedImage(
            String imageFileName, Class<?> relativeTo
    )
    {
        return toBufferedImage(
                relativeTo.getResource(
                        imageFileName ));
    }

    public static BufferedImage toBufferedImage(String imageFileName)
    {
        File imageFile = new File(imageFileName);
        return toBufferedImage(imageFile);
    }

    public static BufferedImage toBufferedImage(File imageFile)
    {
        
        if (! (imageFile.exists()))
        {
            LOG.error("Image file not found: " + imageFile);
            return null;
        }

        try {
            return toBufferedImage(
                    imageFile.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new Error( e );
        }
    }

    public static BufferedImage toBufferedImage(
            URL imageLocation)
    {
        try
        {
            return ImageIO.read( imageLocation );
        }
        catch (IOException e)
        {
            throw Throwables.propagate( e );
        }
    }
    

    //-------------------------------------------------------------------------
    public static boolean hasAlpha(Image image)
    {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage)
        {
            return ((BufferedImage) image).getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try
        {
            pg.grabPixels();
        }
        catch (InterruptedException ignore) {}

        // Get the image's color model
        return pg.getColorModel() != null &&
               pg.getColorModel().hasAlpha();
    }


    //-------------------------------------------------------------------------
    public static Dimension size(BufferedImage image)
    {
        return (image == null)
                ? null
                : new Dimension( image.getWidth(), image.getHeight());
    }


    //-------------------------------------------------------------------------
    public static void save(
            RenderedImage image,
            String        path,
            String        name)
    {
        save(image, path, name, true);
    }

    public static void save(
            RenderedImage image,
            String        path,
            String        name,
            boolean       appendTimestamp)
    {
        save(image, new File(path), name, appendTimestamp);
    }

    public static void save(
            RenderedImage image,
            File          path,
            String        name)
    {
        save(image, path, name, true);
    }

    public static void save(
            RenderedImage image,
            File          path,
            String        name,
            boolean       appendTimestamp)
    {
        try {
            String timeStamp =
                    ! appendTimestamp ? "" :
                    "_" + new DateTime().toString("yyyy.MM.dd.hh.mm.ss");

            File outPath = new File(Dirs.get(path),
                    name + timeStamp + ".png");

            ImageIO.write(image, "png", outPath);
        } catch (IOException err) {
            LOG.error("Unable to capture screen", err);
        }
    }
}
