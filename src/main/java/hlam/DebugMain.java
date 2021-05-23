package hlam;

import ru.lanit.dibr.utils.core.LogSource;
import ru.lanit.dibr.utils.core.SshSource;
import ru.lanit.dibr.utils.gui.LogFrame;
import ru.lanit.dibr.utils.gui.configuration.LogFile;
import ru.lanit.dibr.utils.gui.configuration.SshHost;

import javax.swing.*;
import java.awt.*;

/**
 * User: Vova
 * Date: 14.11.12
 * Time: 2:04
 */
public class DebugMain {
    public static void main(String[] args) {

        LogSource logSource ; //= new TestSource("SystemOut.log");
        logSource = new SshSource(
                new SshHost("test", "sberbank-2.pegacloud.com", 22, "taran", null, "G:\\Dropbox\\work\\PegaCloud\\taran\\taran\\taran.pem", "utf-8", null),
                new LogFile("test", "~/test/SystemOut.log", "\\[\\d\\d?\\/\\d\\d?/\\d\\d? \\d\\d?:\\d\\d?:\\d\\d?:\\d{1,3} MS[DK]\\]", null));

        LogFrame logFrame = new LogFrame(null, null, "DEBUG", logSource, "\\[\\d\\d?\\/\\d\\d?/\\d\\d? \\d\\d?:\\d\\d?:\\d\\d?:\\d{1,3} MS[DK]\\]");
        logFrame.setVisible(true);
        logFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int height = (int) (GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight());
        logFrame.setLocation(200,100);
        logFrame.setSize((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/1.5), (int) (height/1.5));
    }
}
