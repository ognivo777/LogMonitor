package org.obizsoft.logmonitor.config;

import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;

import java.util.Map;

public class Config {
    private Map<String, Object> connections;
    private Map<String, Object> logGroups;

    public Config() {
    }

    public Map<String, Object> getConnections() {
        return connections;
    }

    public void setConnections(Map<String, Object> connections) {
        this.connections = connections;
    }

    public Connection getConnection(String key) {
        return ConfigBeanFactory.create(ConfigFactory.parseMap((Map<String, ? extends Object>) connections.get(key)), Connection.class);
    }

    public Map<String, Object> getLogGroups() {
        return logGroups;
    }

    public void setLogGroups(Map<String, Object> logGroups) {
        this.logGroups = logGroups;
    }

    public LogGroup getLogGroup(String key) {
        return ConfigBeanFactory.create(ConfigFactory.parseMap((Map<String, ? extends Object>) logGroups.get(key)), LogGroup.class);
    }
}
