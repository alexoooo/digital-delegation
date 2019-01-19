package ao.dd.desktop.vision.feature.line;

import java.awt.*;

/**
 * User: 188952
 * Date: Apr 11, 2010
 * Time: 1:04:50 PM
 */
public interface LineMatcher
{
    //-------------------------------------------------------------------------
    /**
     * @param prevLine previous pixel line
     * @param line current pixel line
     * @param nexLine next pixel line
     * @return a score, the greater the better
     *          value must be in [0, 1]
     */
    public double scoreLine(
            Color[] prevLine,
            Color[] line,
            Color[] nexLine);
}