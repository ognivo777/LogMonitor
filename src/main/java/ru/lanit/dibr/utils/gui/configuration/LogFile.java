package ru.lanit.dibr.utils.gui.configuration;

/**
 * User: Vova
 * Date: 23.10.12
 * Time: 1:44
 */
public class LogFile {
    private String name;
    private String path;
    private String blockPattern;
    private String encoding;
    private boolean isLocal = false;

    public LogFile(String name, String path, String blockPattern, String encoding) {
        this.name = name;
        this.path = path;
        this.encoding = encoding;
        this.blockPattern = blockPattern;
    }

    public LogFile(String name, String path, boolean isLocal) {
        this.name = name;
        this.path = path;
        this.isLocal = isLocal;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getBlockPattern() {
        return blockPattern;
    }

    public String getEncoding() {
        return encoding;
    }

    public boolean isLocal() {
        return isLocal;
    }
}
