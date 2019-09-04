package ru.lanit.dibr.utils.gui;

import ru.lanit.dibr.utils.core.LogSource;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * User: VTaran
 * Date: 16.08.2010
 * Time: 17:57:48
 */

public class LogFrame  extends JDialog {

	private Thread t;
	private LogPanel panel;

	public LogFrame(final JButton b, final JComponent c, final String title, final LogSource logSource, final String blockPattern) {
		setTitle(title);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JPanel contentPanel  = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        final LogPanel lp = new LogPanel(logSource, blockPattern);
		panel = lp;
        contentPanel.add(lp);

        contentPanel.add(new FunctionPanel(lp));

        setContentPane(contentPanel);
		t = new Thread() {
			@Override
			public void run() {
                boolean retry = true;
				while(retry) {
                    try {
                        lp.connect();
                    } catch (Exception e) {
                        e.printStackTrace(System.out);
                        System.out.println(e);
    //					JOptionPane.showMessageDialog(LogFrame.this, "Can't open log '" + title + "'!\n" + e.getMessage());
                        Object[] options = {"Yes, please",
                        "No, thanks"};
                        retry = JOptionPane.YES_OPTION == JOptionPane.showOptionDialog(LogFrame.this,
                            "Can't open log '" + title + "'!\n" + e.getMessage() + "\nLet's try to reconnect?",
                            "Error",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE,
                            null,
                            options,
                            options[1]);
                    }
                }

                LogFrame.this.setVisible(false);
                LogFrame.this.setTitle(LogFrame.this.getTitle() + " [ STOPPED ]");

                //ToDO: убрать отсюда эту порнографию!
                if(b!=null) {
                    b.setBorder(new LineBorder(Color.RED));
                    b.setEnabled(false);
                }
                if(c!=null) {
                    c.setEnabled(false);
                }

			}

			@Override
			public void interrupt() {
				lp.close();
			}
		};
		t.start();

	}
}
