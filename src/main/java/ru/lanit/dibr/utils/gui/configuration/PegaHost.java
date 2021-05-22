package ru.lanit.dibr.utils.gui.configuration;

import ru.lanit.dibr.utils.core.AbstractHost;
import ru.lanit.dibr.utils.gui.configuration.LogFile;
import ru.lanit.dibr.utils.gui.configuration.Tunnel;

/**
 * Created by U_M0NJ2 on 16.01.2018.
 */
public class PegaHost extends AbstractHost {

    public PegaHost(String description, String host, String user, String password, String defaultEncoding, Tunnel tunnel) {
        super(description, host, 0, user, password, defaultEncoding, tunnel);
    }

    public PegaHost(String description, Tunnel tunnel, String host,String user, String password, String defaultEncoding, String proxyHost, int proxyPrort, String proxyType, String proxyLogin, String proxyPasswd) {
        super(description, tunnel, host, 0, user, password, defaultEncoding, proxyHost, proxyPrort, proxyType, proxyLogin, proxyPasswd);
    }

    @Override
    public String saveFullFile(LogFile logFile) {
        return null;
    }

    @Override
    public String saveFullFilePlain(LogFile logFile) {
        return null;
    }

}
