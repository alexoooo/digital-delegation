package ao.dd.web.alexo.response;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Utility class.
 */
public class Responses
{
    private Responses() {}

    public static void display(Response response)
    {
        System.out.println(
                "\n\n==================================================");
        System.out.println(response.address());

        if (response instanceof ImageResponse)
        {
            displayImage( ((ImageResponse) response).getImage() );
        }
        else if (response instanceof TextResponse)
        {
            System.out.println(((TextResponse) response).getText());
        }
    }

    private static void displayImage(Image img)
    {
        JDialog d = new JDialog();
        JPanel f = new JPanel();
        f.add(new JLabel(
                new ImageIcon(img)));
        d.setContentPane(f);
        d.pack();
        d.setVisible(true);
    }
}
