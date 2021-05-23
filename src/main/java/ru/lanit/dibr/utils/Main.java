package ru.lanit.dibr.utils;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import ru.lanit.dibr.utils.gui.forms.MainWindow;
import ru.lanit.dibr.utils.utils.JschLogger;

import java.text.NumberFormat;

/**
 * User: VTaran
 * Date: 16.08.2010
 * Time: 15:30:23
 */
public class Main {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Main.class);

    public final static String VERSION = "3.25";

	public static void main(String[] args) {

        log.info("LogMonitor " + VERSION + " starts.");
        JSch.setLogger(new JschLogger());

        log.info("java.home:" + System.getProperties().getProperty("java.home"));

        //todo replace with Shedule at fix rate
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30000);
                    printResourceUsage();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }

        }, "resource usage monitor").start();

        CmdLineParser parser = new CmdLineParser(new CmdLineConfiguration());
        try {
            parser.parseArgument(args);
            Configuration cfg = new Configuration(CmdLineConfiguration.settingsFileName);
            MainWindow mainWindow = new MainWindow(cfg);

            JSch.setLogger(new Logger() {
                @Override
                public boolean isEnabled(int level) {
                    return true;
                }

                @Override
                public void log(int level, String message) {
                    switch (level) {
                        case Logger.DEBUG:
                            log.debug("jsch: " + message);
                            break;
                        case Logger.WARN:
                            log.warn("jsch: " + message);
                            break;
                        case Logger.ERROR:
                            log.error("jsch: " + message);
                            break;
                        case Logger.FATAL:
                            log.fatal("jsch: " + message);
                            break;
                        default:
                            log.info("jsch: " + message);
                    }
                }
            });
        } catch (CmdLineException e) {
            e.printStackTrace();
        }
	}


    public static void printResourceUsage() {
        Runtime runtime = Runtime.getRuntime();

        NumberFormat format = NumberFormat.getInstance();

        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append("free memory: ").append(format.format(freeMemory / 1024)).append("<br/>");
        sb.append("allocated memory: ").append(format.format(allocatedMemory / 1024)).append("<br/>");
        sb.append("max memory: ").append(format.format(maxMemory / 1024)).append("<br/>");
        sb.append("total free memory: ").append(format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024)).append("<br/>");
        log.info(sb);
    }
}
