package ao.dd.desktop.util;

import ao.util.io.AoFiles;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * User: 188952
 * Date: Apr 3, 2010
 * Time: 2:09:25 AM
 */
public class DesktopUtils
{
    //-------------------------------------------------------------------------
    private DesktopUtils() {}


    //-------------------------------------------------------------------------
    public static int abs(int index, int outOf)
    {
        return (index >= 0)
               ? index
               : outOf + index;
    }


    //-------------------------------------------------------------------------
    public static URL getResource(String path)
    {
        return getResource(DesktopUtils.class, path);
    }

    public static URL getResource(
            Class relativeTo, String path)
    {
        return relativeTo.getResource( path );
    }


    public static File getResourceFile(String path)
    {
        return AoFiles.resource(
                DesktopUtils.class, path );
    }

//    public static File getResourceFile(
//            Class relativeTo, String path)
//    {
//        return asFile(relativeTo.getResource( path ));
//    }

//    public static File asFile(URL url)
//    {
//        try {
//            return new File(url.toURI());
//        } catch (URISyntaxException ignored) {
//            try {
//                return new File(url.getPath());
//            } catch (Throwable ignored2) {
//                return null;
//            }
//        }
//    }


    //-------------------------------------------------------------------------
    public static InputStream getResourceStream(String path)
    {
        return getResourceStream(DesktopUtils.class, path );
    }

    public static InputStream getResourceStream(
            Class relativeTo, String path)
    {
        return relativeTo.getResourceAsStream( path );
    }


    //-------------------------------------------------------------------------
    public static Robot newRobot()
    {
        try {
            return new Robot();
        } catch (AWTException e) {
            throw new Error( e );
        }
    }

    public static Robot newRobot(
            GraphicsDevice graphicsDevice)
    {
        try {
            return new Robot( graphicsDevice );
        } catch (AWTException e) {
            throw new Error( e );
        }
    }
}
