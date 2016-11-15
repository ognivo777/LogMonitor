package hlam.newWindow1;

import ru.lanit.dibr.utils.CmdLineConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Vova
 * Date: 31.10.12
 * Time: 2:04
 */
public class JTabbedPaneCustom extends JTabbedPane {
    private JTabbedPane another;
    private static long counter = 0;
    String caption;

    public JTabbedPaneCustom(String caption) {
        this.caption = caption;
    }

    public void setAnother(JTabbedPane another) {
        this.another = another;
    }

    @Override
    public void insertTab(String title, Icon icon, Component component, String tip, int index) {
        super.insertTab(title, icon, component, tip, index);
        JButton goAwayButton = new JButton(caption);
        goAwayButton.setActionCommand((counter++) + ", go out!");

        goAwayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JButton btn = (JButton) e.getSource();
                String a1 = btn.getActionCommand();
                for (int i = 0; i < getTabCount(); i++) {
                    if (((JButton) ((JPanel) getTabComponentAt(i)).getComponent(0)).getActionCommand().equals(a1)) {
                        another.addTab(getTitleAt(i), null);
                        removeTabAt(i);
                        break;
                    }
                }
            }
        });

        JPanel pnl = new JPanel();
        pnl.setPreferredSize(new Dimension(120, 30));
        goAwayButton.setPreferredSize(new Dimension(50, 25));
        goAwayButton.setFont(new Font("Courier New", 0, CmdLineConfiguration.fontSize));
        pnl.setOpaque(false);
        pnl.add(goAwayButton);
        pnl.add(new JLabel(title));
        setTabComponentAt(index, pnl);
    }

    @Override
    public void setTitleAt(int index, String title) {
        super.setTitleAt(index, title);
        ((JLabel)((JPanel) getTabComponentAt(index)).getComponent(1)).setText(title);
    }

    public void moveAll() {
        for (int i = 0; getTabCount()!=0; i++) {
            another.addTab(getTitleAt(0), null);
            removeTabAt(0);
        }
    }
}
