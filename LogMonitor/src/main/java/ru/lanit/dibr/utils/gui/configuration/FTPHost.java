package ru.lanit.dibr.utils.gui.configuration;

import it.sauronsoftware.ftp4j.*;
import ru.lanit.dibr.utils.core.AbstractHost;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vova on 10.04.2015.
 */
public class FTPHost extends AbstractHost {
    public FTPHost(String description, String host, int port, String user, String password, String defaultEncoding, Tunnel tunnel) {
        super(description, host, port, user, password, defaultEncoding, tunnel);
    }

    public FTPHost(String description, Tunnel tunnel, String host, int port, String user, String password, String defaultEncoding, String proxyHost, int proxyPrort, String proxyType, String proxyLogin, String proxyPasswd) {
        super(description, tunnel, host, port, user, password, defaultEncoding, proxyHost, proxyPrort, proxyType, proxyLogin, proxyPasswd);
    }


    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS");

    @Override
    public String saveFullFile(LogFile logFile) {
        try {
            FTPClient client = new FTPClient();
            client.setCompressionEnabled(true);

            client.setType(FTPClient.TYPE_BINARY);
            client.connect(getHost(), getPort());
            client.login(getUser(), getPassword());
            String localFileName = dateFormat.format(new Date()) + ".log";
            client.download(logFile.getPath(), new File(localFileName));
            return localFileName;
        } catch (FTPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
        } catch (FTPAbortedException e) {
            e.printStackTrace();
        } catch (FTPDataTransferException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public String saveFullFilePlain(LogFile logFile) {
        return saveFullFile(logFile);
    }


}
