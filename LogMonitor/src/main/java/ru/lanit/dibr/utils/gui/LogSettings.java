package ru.lanit.dibr.utils.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogSettings {
    private List<FilterEntry> filters = new ArrayList<FilterEntry>();
    private boolean isWrapedLines = false;
    private boolean isShowLineNumbers = false;

    public FilterEntry [] getFilters() {
        return filters.toArray(new FilterEntry[]{});
    }

    public List<FilterEntry> getFiltersList() {
        return filters;
    }

    public void setFilters(FilterEntry[] filters) {
        this.filters.addAll(Arrays.asList(filters));
    }

    public boolean isWrapedLines() {
        return isWrapedLines;
    }

    public void setWrapedLines(boolean isWrapedLines) {
        this.isWrapedLines = isWrapedLines;
    }

    public boolean isShowLineNumbers() {
        return isShowLineNumbers;
    }

    public void setShowLineNumbers(boolean isShowLineNumbers) {
        this.isShowLineNumbers = isShowLineNumbers;
    }


}
