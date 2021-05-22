package ru.lanit.dibr.utils.gui;

import org.apache.commons.lang3.StringEscapeUtils;
import ru.lanit.dibr.utils.core.Filter;
import ru.lanit.dibr.utils.core.TestStringSource;
import ru.lanit.dibr.utils.core.XmlFormatFilter;
import ru.lanit.dibr.utils.core.XmlUnescapeFilter;
import ru.lanit.dibr.utils.utils.XmlUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * User: Vova
 * Date: 26.11.12
 * Time: 2:31
 */
public class PopupBlock extends JFrame {

    public PopupBlock(final String title, final String data, final boolean formatXml) throws Exception {

        setTitle(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        final LogPanel logPanel = new LogPanel(new TestStringSource( formatXml ? XmlUtils.formatXml(data) : data, 0, false), null);
        setContentPane(logPanel);
        logPanel.addAreaKyeListner(new KeyListener() {

            LogPanel nextPanel = null;

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent ke) {
                if ((ke.getModifiers() == KeyEvent.CTRL_MASK ) && ( ke.getKeyCode() == KeyEvent.VK_E) ) {
                    if(nextPanel==null) {
                        String unescapedXml = StringEscapeUtils.unescapeXml(data);
                        nextPanel = new LogPanel(new TestStringSource(formatXml ? XmlUtils.formatXml(unescapedXml) : unescapedXml, 0, false), null);
                        nextPanel.addAreaKyeListner(this);
                        startReadThread(nextPanel);
                    }
                    LogPanel prevContentPanel = (LogPanel) getContentPane();
                    setContentPane(nextPanel);
                    nextPanel = prevContentPanel;
                    setVisible(false);
                    repaint();
                    setVisible(true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        startReadThread(logPanel);
        setSize((int)(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth()* 0.7), (int)(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight()* 0.8));
//        GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds()
        setVisible(true);
    }

    private void startReadThread(final LogPanel logPanel) {
        Thread t = new Thread() {
            @Override
            public void run() {
                boolean retry = true;
                while(retry) {
                    try {
                        logPanel.connect();
                        logPanel.resetFilters();
                    } catch (Exception e) {
                        e.printStackTrace(System.out);
                        System.out.println(e);
                        retry = false;
                    }
                }

                PopupBlock.this.setVisible(false);
            }

            @Override
            public void interrupt() {
                logPanel.close();
            }
        };
        t.start();
    }
}
