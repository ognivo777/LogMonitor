package ru.lanit.dibr.utils.utils;

import com.jcraft.jsch.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Vladimir
 * Date: 26.07.16
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
 */
public class JschLogger implements Logger {

//    public static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JschLogger.class);

    @Override
    public boolean isEnabled(int level) {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void log(int level, String message) {
//        log.info(me);
        System.out.println(message);
    }
}
