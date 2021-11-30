package ru.lanit.dibr.utils.gui.configuration;

import ru.lanit.dibr.utils.core.AbstractHost;

public class LocalSystem extends AbstractHost {

    public LocalSystem(String description, String defaultEncoding) {
        this.description = description;
        this.defaultEncoding = defaultEncoding;
    }

    @Override
    public String saveFullFile(LogFile logFile) {
        return "";
    }

    @Override
    public String saveFullFilePlain(LogFile logFile) {
        return "";
    }
}
