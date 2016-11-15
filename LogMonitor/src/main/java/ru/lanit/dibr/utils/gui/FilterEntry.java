package ru.lanit.dibr.utils.gui;

public class FilterEntry {
    private String pattern;
    private boolean isEnables;

    public FilterEntry() {
    }

    public FilterEntry(String pattern, boolean isEnables) {
        this.pattern = pattern;
        this.isEnables = isEnables;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isEnables() {
        return isEnables;
    }

    public void setEnables(boolean isEnables) {
        this.isEnables = isEnables;
    }
}
