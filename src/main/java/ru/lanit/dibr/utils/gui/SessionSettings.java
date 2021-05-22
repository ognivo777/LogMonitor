package ru.lanit.dibr.utils.gui;

import ru.lanit.dibr.utils.CmdLineConfiguration;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vova on 07.02.14.
 */
public class SessionSettings {
    private Map<String, LogSettings> logSettingsMap = new HashMap<String, LogSettings>();
    private static SessionSettings sessionSettings = null;

    public SessionSettings() {
    }

    public SessionSettings(Map<String, LogSettings> logSettingsMap) {
        this.logSettingsMap = logSettingsMap;
    }

    public void saveSettings() {
        XMLEncoder encoder = null;
        try {
            encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(getFileName())));
            encoder.writeObject(logSettingsMap);
            encoder.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static SessionSettings getInstance() {
        if(sessionSettings==null) {
            XMLDecoder decoder = null;
            try {
                String fileName = getFileName();
                File session;
                if(fileName==null || fileName.trim().isEmpty() || !(session = new File(fileName)).exists() || !session.isFile() || !session.canRead()) {
                    return new SessionSettings(new HashMap<String, LogSettings>());
                }
                decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(fileName)));
                Map<String, LogSettings> logSettingsMap1 = (Map<String, LogSettings>) decoder.readObject();
                decoder.close();
                sessionSettings =  new SessionSettings(logSettingsMap1);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                sessionSettings =  new SessionSettings(new HashMap<String, LogSettings>());
            }
        }
        return  sessionSettings;

    }

    private static String getFileName() {
        return CmdLineConfiguration.settingsFileName.concat(".session.xml");
    }

    public Map<String, LogSettings> getLogSettingsMap() {
        return logSettingsMap;
    }
}

