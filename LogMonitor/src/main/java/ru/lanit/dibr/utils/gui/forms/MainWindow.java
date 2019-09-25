package ru.lanit.dibr.utils.gui.forms;

import ru.lanit.dibr.utils.CmdLineConfiguration;
import ru.lanit.dibr.utils.Configuration;
import ru.lanit.dibr.utils.Main;
import ru.lanit.dibr.utils.core.*;
import ru.lanit.dibr.utils.gui.FunctionPanel;
import ru.lanit.dibr.utils.gui.LogPanel;
import ru.lanit.dibr.utils.gui.MenuButton;
import ru.lanit.dibr.utils.gui.configuration.*;
import ru.lanit.dibr.utils.utils.FileDrop;
import ru.lanit.dibr.utils.utils.Utils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: Vladimir
 * Date: 18.04.14
 * Time: 13:31
 */

public class MainWindow {
    private JFrame window;
    private JPanel rootPanel;
    private JTabbedPane tabbedPane1;
    private JPanel logList;
//    private Configuration configuration;

    public static int logsCnt = 0;

    public MainWindow(Configuration cfg) {
//        configuration = cfg;
        window = new JFrame();
        window.setTitle("Log monitor " + Main.VERSION);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.add(rootPanel);
        window.setSize(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize());

        tabbedPane1.remove(0);

        logList.setLayout(new BoxLayout(logList, BoxLayout.Y_AXIS));
        final LinkedHashMap<Tunnel, List<Label>> tunnelsLabelsMap = new LinkedHashMap<Tunnel, List<Label>>();
        for (final Map.Entry<AbstractHost, LinkedHashMap<String, LogFile>> entry : cfg.getServers().entrySet()) {
            JPanel hostPane = new JPanel();
            hostPane.setLayout(new BoxLayout(hostPane, BoxLayout.Y_AXIS));
            JPanel hostPaneLabel = new JPanel();
            hostPaneLabel.setLayout(new BoxLayout(hostPaneLabel, BoxLayout.X_AXIS));

            //Tunnel prefix
            final Tunnel tunnel = entry.getKey().getTunnel();
            final Label hostLabelPrefix = new Label("[T]", Label.RIGHT);
            if (tunnel != null) {
                hostLabelPrefix.setFont(new Font("Courier", Font.BOLD, CmdLineConfiguration.fontSize + 4));
                hostPaneLabel.add(hostLabelPrefix);
                if (!tunnelsLabelsMap.containsKey(tunnel)) {
                    tunnelsLabelsMap.put(tunnel, new ArrayList<Label>());
                }
                tunnelsLabelsMap.get(tunnel).add(hostLabelPrefix);
            }

            //Host description
            final Label hostLabel = new Label(entry.getKey().getDescription(), Label.LEFT);
            hostLabel.setFont(new Font("Courier", Font.BOLD, CmdLineConfiguration.fontSize + 4));
            hostPaneLabel.add(hostLabel);

            hostPane.add(hostPaneLabel);
            //hostPane.add(hostLabel);


            new Thread(() -> {
                try {
                    while (true) {
                        boolean tunnelAlive = false;
                        if (tunnel != null) {
                            if (tunnel.checkConnection()) {
                                hostLabelPrefix.setForeground(new Color(0x00B32D));
                                tunnelAlive = true;
                            } else {
                                hostLabelPrefix.setForeground(new Color(0xF53D00));
                            }
                        }
                        if (tunnel == null || tunnelAlive) {
                            if (entry.getKey().checkCnnection()) {
                                hostLabel.setForeground(new Color(0x00B32D));
                            } else {
                                hostLabel.setForeground(new Color(0xF53D00));
                            }
                        } else {
                            hostLabel.setForeground(Color.BLACK);
                        }
                        Thread.sleep(1500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            })
//                    .start()
            ;

            JPanel buttons = new JPanel();
            GridBagLayout mgr = new GridBagLayout();
            buttons.setLayout(mgr);
            hostPane.add(buttons);
            for (Map.Entry<String, LogFile> logEntry : entry.getValue().entrySet()) {
                addButton(buttons, logEntry.getValue(), entry.getKey());
                logsCnt++;
            }
            logList.add(hostPane);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        for (Map.Entry<Tunnel, List<Label>> tunnelListEntry : tunnelsLabelsMap.entrySet()) {
                            Color resultColor;
                            if (tunnelListEntry.getKey().checkConnection()) {
                                resultColor = new Color(0x00B32D);
                            } else {
                                resultColor = new Color(0xF53D00);
                            }
                            for (Label label : tunnelListEntry.getValue()) {
                                label.setForeground(resultColor);
                            }
                        }
                        Thread.sleep(1500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        })
//                .start()
        ;


        window.setVisible(true);
    }

    private void addButton(JPanel buttons, final LogFile logFile, final AbstractHost host) {
        final JButton b = new JButton(logFile.getName());
        System.out.println(b.getFont());
        b.setFont(new Font("Courier", 0, CmdLineConfiguration.fontSize + 2));
        b.setBorder(new LineBorder(Color.GRAY));
        final MenuButton menuButton = logFile.isLocal() ? null : new MenuButton(host, logFile, this);
        b.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.paramString());
                LogPanel lp = null;
                if (logFile.isLocal()) {
                    //TODO: реализовать нормальный Source для локальных файлов, используюя org.apache.commons.io.input.Tailer
                    //lp = new LogFrame(b, menuButton, logFile.getName(), new TestSource(logFile.getPath()), logFile.getBlockPattern());
                } else {
                    //TODO: !!! убрать хосты внутрь лог файлов! и создавать соурсы методом на файлах!
                    if (host instanceof SshHost) {
                        lp = new LogPanel(new SshSource((SshHost) host, logFile), logFile.getBlockPattern());
                    } else if (host instanceof FTPHost) {
                        lp = new LogPanel(new FtpSource((FTPHost) host, logFile), logFile.getBlockPattern());
                    } else if (host instanceof CIFSHost) {
                        lp = new LogPanel(new CIFSSource((CIFSHost) host, logFile), logFile.getBlockPattern());
                    } else if (host instanceof PegaHost) {
                        lp = new LogPanel(new PegaSource((PegaHost) host), logFile.getBlockPattern());
                    } else if (host instanceof SocketHubHost) {
                        lp = new LogPanel(new SocketHubClientSource(host), SocketHubClientSource.BLOCK_PATTERN);
                    }
                    createTab(lp, host.getDescription() + " : " + logFile.getName());
                    new FileDrop(System.out, lp.getViewport().getView(), new FileDrop.Listener() {
                        @Override
                        public void filesDropped(File[] files) {
                            for (int i = 0; i < files.length; i++) {
                                createTab(new LogPanel(new SimpleLocalFileSource(files[i].getAbsolutePath(), 0), logFile.getBlockPattern()), "[" + files[i].getName() + "]");
                            }
                        }
                    });
                }
                lp.getViewport().getView().requestFocusInWindow();
            }

        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridx = 0;

        buttons.add(b, gbc);

        gbc.gridx++;
        if (menuButton != null) {
            buttons.add(menuButton, gbc);
        }
    }

    public Component createTab(final LogPanel lp, final String name) {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(lp);
        contentPanel.add(new FunctionPanel(lp));
        final AtomicBoolean isManuallyClosed = new AtomicBoolean(false);
        final Thread connectionAlertThread = new Thread("ConnAlert: " + name) {
            @Override
            public void run() {
                boolean retry = true;
                while (retry) {
                    retry = false;
                    try {
                        lp.connect();
                    } catch (Exception e) {
                        e.printStackTrace(System.out);
                        if (!isManuallyClosed.get()) {
                            Object[] options = {"Yes, please",
                                    "No, thanks"};
                            retry = JOptionPane.YES_OPTION == JOptionPane.showOptionDialog(lp,
                                    "Can't open log '" + name + "'!\n" + e.getMessage() + "\n" + Utils.getFirstCause(e).getMessage() + "\nLet's try to reconnect?",
                                    "Error",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.ERROR_MESSAGE,
                                    null,
                                    options,
                                    options[1]);
                        }
                    }
                }

            }
        };
        connectionAlertThread.start();
        final Component newTab = tabbedPane1.add(name, contentPanel);
        tabbedPane1.setSelectedComponent(newTab);

        JPanel pnl = new JPanel();
        JLabel label = new JLabel(name + " ");
        label.setFont(new Font("Courier New", 0, CmdLineConfiguration.fontSize));
        pnl.add(label);
        ((FlowLayout) pnl.getLayout()).setVgap(0);
        ((FlowLayout) pnl.getLayout()).setHgap(0);
        JButton goAwayButton = new JButton("X");
        goAwayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane1.remove(newTab);
                isManuallyClosed.set(true);
                lp.close();
            }
        });
        goAwayButton.setMargin(new Insets(0, 0, 0, 0));
        int closeBtnSize = (int) (CmdLineConfiguration.fontSize * 1.2);
        goAwayButton.setPreferredSize(new Dimension(closeBtnSize, closeBtnSize));
        goAwayButton.setFont(new Font("Courier New", 0, CmdLineConfiguration.fontSize));
        pnl.setOpaque(false);
        pnl.add(goAwayButton);
        tabbedPane1.setTabComponentAt(tabbedPane1.getSelectedIndex(), pnl);

        return newTab;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        rootPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(78, 14), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        logList = new JPanel();
        logList.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane1.setViewportView(logList);
        logList.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        tabbedPane1 = new JTabbedPane();
        rootPanel.add(tabbedPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Untitled", panel2);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
