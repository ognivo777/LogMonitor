package test;

import ru.lanit.dibr.utils.core.FtpSource;
import ru.lanit.dibr.utils.gui.configuration.FTPHost;
import ru.lanit.dibr.utils.gui.configuration.LogFile;

/**
 * Created by Vova on 10.04.2015.
 */
public class TestFTPSource {
    public static void main(String[] args) throws Exception {
        FTPHost host = new FTPHost("1", "127.0.0.1", 2221, "user", "password", "utf-8", null);
        LogFile log = new LogFile("2", "tst.log", false);
        FtpSource source = new FtpSource(host, log);
        source.startRead();

        while (true) {
            System.out.println(source.readLine());
        }
    }
}
