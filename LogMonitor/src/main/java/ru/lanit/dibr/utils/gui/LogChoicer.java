package ru.lanit.dibr.utils.gui;

import ru.lanit.dibr.utils.CmdLineConfiguration;
import ru.lanit.dibr.utils.Configuration;
import ru.lanit.dibr.utils.core.AbstractHost;
import ru.lanit.dibr.utils.core.SshSource;
import ru.lanit.dibr.utils.core.SimpleLocalFileSource;
import ru.lanit.dibr.utils.gui.configuration.SshHost;
import ru.lanit.dibr.utils.gui.configuration.LogFile;
import ru.lanit.dibr.utils.utils.FileDrop;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: VTaran
 * Date: 16.08.2010
 * Time: 18:34:16
 */
@Deprecated
public class LogChoicer extends JFrame implements WindowStateListener {
    public static int size;
    public static int countShownLogWindow = 0;
    public static int logsCnt = 0;
	private JPanel pane;
    private final java.util.List<LogFrame> logs = new ArrayList<LogFrame>();
    public JPanel localFilesButtons;


    public LogChoicer(Configuration cfg) throws HeadlessException {
		setTitle("Log monitor 3.6");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowStateListener(this);
		pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        setLocation((int) GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getLocation().getX(), 250);
		for (Map.Entry<AbstractHost, LinkedHashMap<String, LogFile>> entry : cfg.getServers().entrySet()) {
			JPanel hostPane = new JPanel();
			hostPane.setLayout(new BoxLayout(hostPane, BoxLayout.Y_AXIS));
			Label hostLabel = new Label(entry.getKey().getDescription(), Label.CENTER);
			hostLabel.setFont(new Font("Courier", Font.BOLD, CmdLineConfiguration.fontSize+4));
			hostPane.add(hostLabel);
			JPanel buttons = new JPanel();
			GridBagLayout mgr = new GridBagLayout();
			buttons.setLayout(mgr);
			hostPane.add(buttons);
			for (Map.Entry<String, LogFile> logEntry : entry.getValue().entrySet()) {
				addButton(buttons,  logEntry.getValue(), entry.getKey());
                logsCnt++;
			}
			pane.add(hostPane);
		}
        new FileDrop(System.out, pane, /*dragBorder,*/ new FileDrop.Listener() {
            java.util.List<String> filesPaths = new ArrayList<String>();
            public void filesDropped(java.io.File[] files) {
                for (int i = 0; i < files.length; i++) {
                    String path = files[i].getAbsolutePath();
                    if(this.filesPaths.contains(path)) {
                        JOptionPane.showMessageDialog(pane, "File '" + path +"' has already added!","Local file addition error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    this.filesPaths.add(path);
                    if(localFilesButtons == null) {
                        JPanel localFilesPane;
                        //TODO: remove that copy-pasted strings to method
                        localFilesPane = new JPanel();
                        localFilesPane.setLayout(new BoxLayout(localFilesPane, BoxLayout.Y_AXIS));
                        Label hostLabel = new Label("Local files(NOT tailed!)");
                        hostLabel.setFont(new Font("Courier", Font.BOLD, CmdLineConfiguration.fontSize + 4));
                        localFilesPane.add(hostLabel);
                        localFilesButtons = new JPanel();
                        GridBagLayout mgr = new GridBagLayout();
                        localFilesButtons.setLayout(mgr);
                        localFilesPane.add(localFilesButtons);
                        pane.add(localFilesPane);
                    }

                    addButton(localFilesButtons,  new LogFile(files[i].getName(), path, true), null);
                    logsCnt++;
                    pack();
                }
            }
        });
        setContentPane(pane);
		pack();
        setResizable(false);
        size = getWidth();
        

//		setSize(300, getHeight());
	}

	private void addButton(JPanel buttons, final LogFile logFile, final AbstractHost host) {
        final JButton b = new JButton(logFile.getName());
        System.out.println(b.getFont());
        b.setFont(new Font("Courier", 0, CmdLineConfiguration.fontSize+2));
        b.setBorder(new LineBorder(Color.GRAY));
//        final MenuButton menuButton = logFile.isLocal()? null : new MenuButton(host, logFile.getPath(), logFile.getName(), null, null);
        final MenuButton menuButton = logFile.isLocal()? null : new MenuButton(host, logFile, null);
		b.addActionListener(new AbstractAction() {
			LogFrame lf = null;
			public void actionPerformed(ActionEvent e) {
				System.out.println(e.paramString());
				if(lf==null) {
					if(logFile.isLocal()) {
                        //TODO: реализовать нормальный Source для локальных файлов, используюя org.apache.commons.io.input.Tailer
                        lf = new LogFrame(b, menuButton, logFile.getName(), new SimpleLocalFileSource(logFile.getPath()), logFile.getBlockPattern());
                    } else {
                        lf = new LogFrame(b, menuButton, host.getDescription()+ " : " + logFile.getName(), new SshSource((SshHost) host, logFile), logFile.getBlockPattern());
                    }
                    lf.setVisible(true);
					b.setForeground(new Color(48, 129, 97));
                    b.setBorder(new LineBorder(new Color(48, 129, 97)));
                    b.setText(logFile.getName());
					logs.add(lf);
                    lf.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            lf.setVisible(false);
                                b.setBorder(new LineBorder(Color.GRAY));
                                b.setForeground(Color.GRAY);
                            b.setText(logFile.getName());
                        }
                    });
				}
				else {
                    lf.setVisible(!lf.isVisible());
                    if(lf.isVisible()) {
                        b.setBorder(new LineBorder(new Color(48, 129, 97)));
                        b.setForeground(new Color(48, 129, 97));
                    } else {
                        b.setBorder(new LineBorder(Color.GRAY));
                        b.setForeground(Color.GRAY);
                    }
                    b.setText(logFile.getName());
				}

                //auto arrange visible windows
                ArrayList<LogFrame>  visibleWindows = new ArrayList<LogFrame>();
                for (LogFrame logWindow : logs) {
                    if(logWindow.isVisible()) {
                        visibleWindows.add(logWindow);
                    }
                }
                int height = (int) (GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight()/visibleWindows.size());
                int width= (int) (GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth());
                Point zeroLocation = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getLocation();
                int i = 0;
                for (LogFrame visibleWindow : visibleWindows) {
                    visibleWindow.setLocation((int) (zeroLocation.getX() + LogChoicer.size),(int)((i++)*height));
                    visibleWindow.setSize((int)(width - LogChoicer.size), height);
                }

			}
		});

        GridBagConstraints gbc =  new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridx = 0;

		buttons.add(b, gbc);

        gbc.gridx++;
        if(menuButton!=null) {
            buttons.add(menuButton, gbc);
        }
	}

    @Override
    public void windowStateChanged(WindowEvent e) {
        if(e.getNewState() == 1) {
            for (LogFrame log : logs) {
                log.setVisible(false);
            }
        } else if( e.getNewState() == 0) {
            for (LogFrame log : logs) {
                log.setVisible(true);
            }
        }
    }
}
