package org.obizsoft.logmonitor.config;

public class LogFormat {
    private String blockPattern;
    private String timestampPattern;

    public LogFormat() {
    }

    public String getBlockPattern() {
        return blockPattern;
    }

    public void setBlockPattern(String blockPattern) {
        this.blockPattern = blockPattern;
    }

    public String getTimestampPattern() {
        return timestampPattern;
    }

    public void setTimestampPattern(String timestampPattern) {
        this.timestampPattern = timestampPattern;
    }
}
