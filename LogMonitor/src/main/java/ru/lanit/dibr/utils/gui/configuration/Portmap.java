package ru.lanit.dibr.utils.gui.configuration;

/**
 * User: Vova
 * Date: 22.10.13
 * Time: 1:47
 */
public class Portmap {
    private int localPort;
    private String destHost;
    private int destPort;

    public Portmap(int localPort, String destHost, int destPort) {
        this.localPort = localPort;
        this.destHost = destHost;
        this.destPort = destPort;
    }

    public int getLocalPort() {
        return localPort;
    }

    public String getDestHost() {
        return destHost;
    }

    public int getDestPort() {
        return destPort;
    }

}
