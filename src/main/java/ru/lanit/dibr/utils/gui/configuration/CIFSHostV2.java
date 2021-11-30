package ru.lanit.dibr.utils.gui.configuration;

import ru.lanit.dibr.utils.core.AbstractHost;

/**
 * Created by Vova on 17.02.2016.
 */
public class CIFSHostV2 extends AbstractHost {

    private String domain;
    private String shareName;

    public CIFSHostV2(String description, String host, int port, String user, String password, String defaultEncoding, Tunnel tunnel) {
        super(description, host, port, user, password, defaultEncoding, tunnel);
    }

    public CIFSHostV2(String description, Tunnel tunnel, String host, int port, String user, String password, String defaultEncoding, String proxyHost, int proxyPrort, String proxyType, String proxyLogin, String proxyPasswd) {
        super(description, tunnel, host, port, user, password, defaultEncoding, proxyHost, proxyPrort, proxyType, proxyLogin, proxyPasswd);
    }

    @Override
    public String saveFullFile(LogFile logFile) {
        return null;
    }

    @Override
    public String saveFullFilePlain(LogFile logFile) {
        return null;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getShareName() {
        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
    }
}
