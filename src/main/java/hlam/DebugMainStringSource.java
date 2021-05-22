package hlam;

import ru.lanit.dibr.utils.core.LogSource;
import ru.lanit.dibr.utils.core.TestStringSource;
import ru.lanit.dibr.utils.gui.LogFrame;

import javax.swing.*;
import java.awt.*;

/**
 * User: Vova
 * Date: 14.11.12
 * Time: 2:04
 */
public class DebugMainStringSource {
    public static void main(String[] args) {

        LogSource logSource = new TestStringSource("" +
                "[block] \n" +
                "11 aaa\n" +
                "[block] \n" +
                "11 bbb\n" +
                "[block] \n" +
                "11 ccc\n" +
                "[block] \n" +
                "22 aaa\n" +
                "[block] \n" +
                "22 bbb\n" +
                "[block] \n" +
                "22 ccc\n" +
//                "[block]" +
                ""
        );

        LogFrame logFrame = new LogFrame(null, null, "DEBUG", logSource, "\\[block\\]");
        logFrame.setVisible(true);
        logFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int height = (int) (GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight());
        logFrame.setLocation(200,100);
        logFrame.setSize((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/1.5), (int) (height/1.5));

    }
}
