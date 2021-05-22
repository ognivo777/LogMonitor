package test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Vova on 15.02.2015.
 */
public class LogGenerator {

    public static void main(String[] args) throws IOException, InterruptedException {
        String filename = "test.log";
        generateTo(filename);

    }

    public static void generateTo(String filename) throws IOException, InterruptedException {
        File oldLog = new File(filename);
        if(oldLog.exists()) {
            oldLog.delete();
        }
        FileWriter writer  = new FileWriter(filename);
        long num = 0;
        while(true) {
            Thread.sleep(1500);
            String s = "\n[" + System.currentTimeMillis() + "] line \n№" + num++;
            System.out.println(s);
            writer.append(s);
            writer.flush();
        }
    }
}
