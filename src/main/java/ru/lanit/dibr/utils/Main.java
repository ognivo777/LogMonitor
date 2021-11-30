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

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
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

//            LogChoicer logs = new LogChoicer(cfg);
//            logs.setVisible(true);
            JSch.setLogger(new Logger() {
                @Override
                public boolean isEnabled(int level) {
                    return true;
                }

                @Override
                public void log(int level, String message) {
                    System.out.println(level + ":" + message);
//                    new Exception().printStackTrace();
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

        sb.append("free memory: " + format.format(freeMemory / 1024) + "<br/>");
        sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "<br/>");
        sb.append("max memory: " + format.format(maxMemory / 1024) + "<br/>");
        sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "<br/>");
//        System.out.println(sb);
        log.info(sb);
    }
}
