package ru.lanit.dibr.utils.gui;

import it.sauronsoftware.ftp4j.*;
import ru.lanit.dibr.utils.core.AbstractHost;
import ru.lanit.dibr.utils.core.SimpleLocalFileSource;
import ru.lanit.dibr.utils.gui.configuration.FTPHost;
import ru.lanit.dibr.utils.gui.configuration.LogFile;
import ru.lanit.dibr.utils.gui.configuration.SshHost;
import ru.lanit.dibr.utils.gui.forms.MainWindow;
import ru.lanit.dibr.utils.utils.ScpUtils;
import ru.lanit.dibr.utils.utils.SshUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jcraft.jsch.JSchException;

/**
 * User: Vova
 * Date: 07.09.2010
 * Time: 23:47:43
 */
public class MenuButton extends JButton {
    private LogFile logFile = null;
    private AbstractHost host = null;


    public MenuButton(final AbstractHost host, final LogFile logFile, final MainWindow mainWindow) {
        this.logFile = logFile;
        this.host = host;

        setText("...");
        setPreferredSize(new Dimension(15, 15));

        final JPopupMenu opts = new JPopupMenu();
        opts.setInvoker(MenuButton.this);

        JMenuItem menuItem;
        // Меню ======== СОХРАНИТЬ ============
        //TODO ПЕРЕПИСАТЬ host на AbstractHost!!! Логику скачивания полного файла унести в хост или сорс!
        menuItem = new JMenuItem("Сохранить весь файл");
        menuItem.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                host.saveFullFile(logFile);

            }
        });
        opts.add(menuItem);
        menuItem = new JMenuItem("Сохранить весь файл и открыть в табе");

        menuItem.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                String fileName = host.saveFullFilePlain(logFile);
                mainWindow.createTab(new LogPanel(new SimpleLocalFileSource(fileName, 0), logFile.getBlockPattern()), host.getDescription() + ": [saved] " + logFile.getPath());
            }
        });
        opts.add(menuItem);


        //  Показ меню
        addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (opts.isVisible()) {
                    opts.setVisible(false);
                    //opts.updateUI();
                } else {
                    opts.show(MenuButton.this, 10, 10);
                }
            }
        });
    }

}

