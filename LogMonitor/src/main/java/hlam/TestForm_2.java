package hlam;

import javax.swing.*;
import java.awt.*;

/**
 * User: Vova
 * Date: 21.08.2010
 * Time: 1:36:43
 */
public class TestForm_2 extends JFrame {
    public static void main(String[] args) {
        JFrame frame = new TestForm_2();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        JPanel pane = new JPanel();
        frame.setContentPane(pane);

        GridBagLayout bagLayout = new GridBagLayout();

        GridBagConstraints c =  new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        pane.setLayout(bagLayout);

        for(int i = 0; i<10 ; i++) {
            c.gridy = i;
            c.gridx = 0;
            pane.add(new JButton("B " + Math.pow(8, i)), c);
            c.gridx = 1;
            pane.add(new JCheckBox("C " + i), c);
        }
        frame.pack();

    }
}
