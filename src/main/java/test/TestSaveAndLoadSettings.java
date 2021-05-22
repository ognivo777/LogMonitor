package test;

import org.junit.Assert;
import org.junit.Test;
import ru.lanit.dibr.utils.CmdLineConfiguration;
import ru.lanit.dibr.utils.gui.FilterEntry;
import ru.lanit.dibr.utils.gui.LogSettings;
import ru.lanit.dibr.utils.gui.SessionSettings;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vova on 07.02.14.
 */
public class TestSaveAndLoadSettings {
    @Test
    public void testSettings() throws FileNotFoundException {
        CmdLineConfiguration.settingsFileName = "s1.xml";
        SessionSettings settings = new SessionSettings();
        LogSettings logSettings = new LogSettings();

        List<FilterEntry> filters = new ArrayList<FilterEntry>();

        filters.add(new FilterEntry("filter something", true));
        filters.add(new FilterEntry("don't filter something", false));
        logSettings.setFilters(filters.toArray(new FilterEntry[]{}));
        logSettings.setShowLineNumbers(false);
        logSettings.setWrapedLines(true);

        settings.getLogSettingsMap().put("test", logSettings);

        settings.saveSettings();

        SessionSettings loadedSettings = SessionSettings.getInstance();
        LogSettings logSettingsLoaded = loadedSettings.getLogSettingsMap().get("test");
        Assert.assertEquals(logSettingsLoaded.getFilters()[0].getPattern(), logSettings.getFilters()[0].getPattern());
        Assert.assertEquals(logSettingsLoaded.getFilters()[0].isEnables(), logSettings.getFilters()[0].isEnables());
        Assert.assertEquals(logSettingsLoaded.isShowLineNumbers(), logSettings.isShowLineNumbers());
        Assert.assertEquals(logSettingsLoaded.isWrapedLines(), logSettings.isWrapedLines());

    }

}
