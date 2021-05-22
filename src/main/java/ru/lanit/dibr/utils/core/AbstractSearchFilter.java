package ru.lanit.dibr.utils.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vova
 * Date: 13.11.12
 * Time: 2:43
 */
public abstract class AbstractSearchFilter extends AbstractFilter implements SearchFilter {

    protected boolean inverted = false;
    protected List<String> stringsToSearch = new ArrayList<String>();

    protected AbstractSearchFilter(boolean inverted) {
        this.inverted = inverted;
    }

    protected AbstractSearchFilter(String pattern, boolean inverted) {
        this.inverted = inverted;
        this.addStringToSearch(pattern);
        this.isValid = true;
    }

    private boolean isValid = false;

    public void disable() {
        stringsToSearch.clear();
        isValid = false;
    }

    public boolean isActive() {
        return isValid;
    }

    protected String removeLineNumbers(String selected) {
        selected = selected.replaceAll("^[\\s\\d]*:\\s", "");
        selected = selected.replaceAll("\n[\\s\\d]*:\\s", "\n");
        return selected;
    }

    public AbstractSearchFilter addStringToSearch(String str) {
        isValid = true;
        stringsToSearch.add(str);
        return this;
    }

    public void removeStringFromSearch(String str) {
        stringsToSearch.remove(str);
    }

    public List<String> getStringsToSearch() {
        return stringsToSearch;
    }
}
