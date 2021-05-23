package org.obizsoft.logmonitor.config;

import com.typesafe.config.Optional;

public class Connection {
    private String description;
    private String host;
    private int port;
    private Auth auth;
    @Optional
    private Connection over;

    public Connection() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public Connection getOver() {
        return over;
    }

    public void setOver(Connection over) {
        this.over = over;
    }
}
