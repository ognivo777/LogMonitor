package ru.lanit.dibr.utils;

import com.jcraft.jsch.JSch;
import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import ru.lanit.dibr.utils.gui.LogChoicer;
import ru.lanit.dibr.utils.gui.forms.MainWindow;
import ru.lanit.dibr.utils.utils.JschLogger;

import java.text.NumberFormat;

/**
 * User: VTaran
 * Date: 16.08.2010
 * Time: 15:30:23
 */
public class Main {

    private static Logger log = Logger.getLogger(ru.lanit.dibr.utils.Main.class);


    public final static String VERSION = "3.17";

	public static void main(String[] args) {

        //log.info("teeeest");
        JSch.setLogger(new JschLogger());

        System.out.println(System.getProperties().getProperty("java.home") );

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                        printResourceUsage();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
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
        System.out.println(sb);
    }
}
