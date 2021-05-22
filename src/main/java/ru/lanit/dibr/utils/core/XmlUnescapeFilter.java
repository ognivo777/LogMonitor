package ru.lanit.dibr.utils.core;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;

/**
 * Created by Vova on 18.12.2015.
 */
public class XmlUnescapeFilter extends AbstractFilter {
    @Override
    protected String readFilteredLine(Source source) throws IOException {
        String result = StringEscapeUtils.unescapeXml(source.readLine());
        System.out.println(result);
        return result;
    }
}
