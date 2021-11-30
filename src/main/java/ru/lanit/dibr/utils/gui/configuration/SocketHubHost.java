package ru.lanit.dibr.utils.gui.configuration;

import ru.lanit.dibr.utils.core.AbstractHost;

public class SocketHubHost extends AbstractHost {

    public SocketHubHost(String description, String host, int port){
        this.description = description;
        this.host = host;
        this.port = port;
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
