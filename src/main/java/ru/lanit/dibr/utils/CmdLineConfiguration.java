package ru.lanit.dibr.utils;


import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * Created with IntelliJ IDEA.
 * User: Vladimir
 * Date: 06.02.14
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */
public final class CmdLineConfiguration {
    public static final int DEFAULT_FONT_SIZE = 12;

    @Option(name = "-fontSize", usage = "Font size")
    public static int fontSize = DEFAULT_FONT_SIZE;

    @Argument(usage = "Name of XML config file. Default value: settings.xml")
    public static String settingsFileName = "settings.xml";

}
