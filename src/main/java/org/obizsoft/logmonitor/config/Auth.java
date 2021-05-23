package org.obizsoft.logmonitor.config;

import com.typesafe.config.Optional;

public class Auth {
    private String login;
    private String password;
    @Optional
    private String pem;

    public Auth() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPem() {
        return pem;
    }

    public void setPem(String pem) {
        this.pem = pem;
    }
}
