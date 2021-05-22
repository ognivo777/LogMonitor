package ru.lanit.dibr.utils.core;

import java.io.IOException;
import java.util.Arrays;

/**
 * User: Vova
 * Date: 13.11.12
 * Time: 2:43
 */
public class LineSearchFilter extends AbstractSearchFilter {

    public LineSearchFilter(String pattern, boolean inverted) {
        super(pattern, inverted);
        stringsToSearch.add(pattern);
    }

    public LineSearchFilter(boolean inverted, String... pattern) {
        super(pattern[0], inverted);
        stringsToSearch.addAll(Arrays.asList(pattern));
    }

    public LineSearchFilter(boolean inverted) {
        super(inverted);
    }

    @Override
    protected String readFilteredLine(Source source) throws IOException {
        String nextLine;
        if ((nextLine = source.readLine()) != null && nextLine!=LogSource.SingletonSkipLineValue.SKIP_LINE) {
            boolean oneOfStringsIsFound = false;
            for (String nextString : stringsToSearch) {
                if (oneOfStringsIsFound = removeLineNumbers(nextLine).contains(nextString)) {
                    break;
                }
            }
            if(oneOfStringsIsFound^inverted) {
                return nextLine;
            }
        }
        return LogSource.SingletonSkipLineValue.SKIP_LINE;
    }
}
