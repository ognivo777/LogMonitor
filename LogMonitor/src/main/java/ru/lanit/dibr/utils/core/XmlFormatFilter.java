package ru.lanit.dibr.utils.core;

import ru.lanit.dibr.utils.utils.XmlUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Vova
 * Date: 16.11.12
 * Time: 0:19
 */
public class XmlFormatFilter extends BlockSearchFilter {

    private Pattern searchXml = Pattern.compile("(<.+>)+", Pattern.MULTILINE|Pattern.DOTALL);

    public XmlFormatFilter(String blockPattern) {
        super(blockPattern, null, true);
    }

    @Override
    protected String readFilteredLine(Source source) throws IOException {
        String line = super.readFilteredLine(source);
        line = removeLineNumbers(line);
        if(line!=LogSource.SingletonSkipLineValue.SKIP_LINE) {
            Matcher m = searchXml.matcher(line);
            if(m.find()) {
                StringBuilder lineBuilder = new StringBuilder(line);
                String xml = m.group(0);
                int start = m.start(0);
                lineBuilder.replace(start, start+xml.length(), "\n" + XmlUtils.formatXml(xml));
                return lineBuilder.toString();
            }
        }
        return line;
    }
}
