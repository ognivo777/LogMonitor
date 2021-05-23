package org.obizsoft.logmonitor.config;

import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.Optional;

import java.util.Map;

public class LogGroup {
    private Map<String, Object> files;
    @Optional
    private Map<String, Object> subgroups;

    public LogGroup() {
    }

    public Map<String, Object> getFiles() {
        return files;
    }

    public void setFiles(Map<String, Object> files) {
        this.files = files;
    }

    public Map<String, Object> getSubgroups() {
        return subgroups;
    }

    public void setSubgroups(Map<String, Object> subgroups) {
        this.subgroups = subgroups;
    }

    public File getFile(String key) {
        return ConfigBeanFactory.create(ConfigFactory.parseMap((Map<String, ? extends Object>) files.get(key)), File.class);
    }

    public LogGroup getLogGroup(String key) {
        return ConfigBeanFactory.create(ConfigFactory.parseMap((Map<String, ? extends Object>) subgroups.get(key)), LogGroup.class);
    }
}
