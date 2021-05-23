package org.obizsoft.logmonitor.config;

import com.typesafe.config.Optional;

public class File {
    private String name;
    @Optional
    private String encoding;
    @Optional
    private LogFormat format;
    @Optional
    private Connection connection;

    public File() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public LogFormat getFormat() {
        return format;
    }

    public void setFormat(LogFormat format) {
        this.format = format;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
