package ru.lanit.dibr.utils.core;

import java.util.List;

/**
 * User: Vova
 * Date: 13.11.12
 * Time: 2:10
 */
public interface SearchFilter extends Filter {
    public SearchFilter addStringToSearch(String str);
    public List<String> getStringsToSearch();
    public void removeStringFromSearch(String str);
}
