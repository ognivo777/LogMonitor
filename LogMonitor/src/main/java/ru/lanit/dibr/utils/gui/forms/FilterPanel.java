package ru.lanit.dibr.utils.gui.forms;

import ru.lanit.dibr.utils.core.SearchFilter;
import ru.lanit.dibr.utils.gui.FilterEntry;
import ru.lanit.dibr.utils.gui.LogPanel;
import ru.lanit.dibr.utils.gui.LogSettings;
import ru.lanit.dibr.utils.gui.SessionSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * User: Vova
 * Date: 12.11.13
 * Time: 1:12
 */
public class FilterPanel extends JPanel {
    private JPanel panel1;
    private JButton delButton;
    private JButton addButton;
    private JButton clearButton;
    private CheckBoxList checkBoxList;
    private JCheckBox checkBox1;
    private JLabel label1;
    private JButton editButton;

    private List<JCheckBox> checkBoxesList = new ArrayList<JCheckBox>();
    private Map<String, JCheckBox> checkBoxesMap = new HashMap<String, JCheckBox>();
    private SearchFilter filter;
    private LogPanel lp;
    private SessionSettings settings;
    private List<FilterEntry> filterEntries;

    public void init(final LogPanel logPanel) {
        this.lp = logPanel;

        settings = SessionSettings.getInstance();
        LogSettings logSettings = settings.getLogSettingsMap().get(getFilterSettingsKey());
        if (logSettings == null) {
            logSettings = new LogSettings();
            settings.getLogSettingsMap().put(getFilterSettingsKey(), logSettings);
        }
        filterEntries = logSettings.getFiltersList();
        for (FilterEntry filterEntry : filterEntries) {
            addFilter(filterEntry.getPattern());
        }

    }

    public FilterPanel(String title) throws HeadlessException {
        label1.setText(title);
        add(panel1);
        setVisible(true);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pattern = (String) JOptionPane.showInputDialog(panel1, "Pattern" + ":\n", "Pattern", JOptionPane.INFORMATION_MESSAGE, null, null, null);
                add(pattern);
            }
        });

        delButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = checkBoxList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    del(selectedIndex);
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = checkBoxList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    String curPattern = del(selectedIndex);
                    String pattern = (String) JOptionPane.showInputDialog(panel1, "Pattern" + ":\n", "Pattern", JOptionPane.INFORMATION_MESSAGE, null, null, curPattern);
                    add(pattern);
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkBoxesMap.clear();
                checkBoxesList.clear();
                checkBoxList.clear();
                filterEntries.clear();
                settings.saveSettings();
            }
        });

        checkBox1.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                checkBoxList.markAll(checkBox1.isSelected());
            }
        });
    }

    private void add(String pattern) {
        if (pattern != null && !(pattern = pattern.trim()).isEmpty() && !checkBoxesMap.containsKey(pattern)) {
            addFilterAndSave(pattern);
        }
    }

    private String del(int selectedIndex) {
        checkBoxesMap.remove(checkBoxesList.remove(selectedIndex).getText());
        checkBoxList.removeCheckbox(selectedIndex);
        String removedPattern = filterEntries.remove(selectedIndex).getPattern();
        settings.saveSettings();
        return removedPattern;
    }

    private String getFilterSettingsKey() {
        return lp.getLogSourceName() + "|" + label1.getText();
    }

    private void addFilterAndSave(String pattern) {
        addFilter(pattern);
        //TODO: save check bos states so
        filterEntries.add(new FilterEntry(pattern, false));
        settings.saveSettings();
    }

    private void addFilter(String pattern) {
        JCheckBox checkBox = new JCheckBox(pattern, true);
        checkBoxList.addCheckbox(checkBox);
        checkBoxesList.add(checkBox);
        checkBoxesMap.put(pattern, checkBox);
    }

    public void applyFilter(final SearchFilter filter) {
        this.filter = filter;
        for (JCheckBox jCheckBox : checkBoxesList) {
            jCheckBox.setSelected(false);
        }
        for (final String s : filter.getStringsToSearch()) {
            if (!checkBoxesMap.containsKey(s)) {
                addFilterAndSave(s);
            } else {
                checkBoxesMap.get(s).setSelected(true);
            }
        }
    }

    public void apply() {
        filter.disable();
        for (JCheckBox jCheckBox : checkBoxesList) {
            if (jCheckBox.isSelected()) {
                filter.addStringToSearch(jCheckBox.getText());
            } else {
                filter.removeStringFromSearch(jCheckBox.getText());
            }
        }
        lp.resetFilters();
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
        panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setBackground(UIManager.getColor("ArrowButton.borderDisabledColor"));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-39424)), null));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), null));
        delButton = new JButton();
        delButton.setText("Del");
        panel2.add(delButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 15), new Dimension(-1, 15), new Dimension(-1, 15), 0, false));
        addButton = new JButton();
        addButton.setText("Add");
        panel2.add(addButton, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 15), new Dimension(-1, 15), new Dimension(-1, 15), 0, false));
        clearButton = new JButton();
        clearButton.setText("Clear");
        panel2.add(clearButton, new com.intellij.uiDesigner.core.GridConstraints(0, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 15), new Dimension(-1, 15), new Dimension(-1, 15), 0, false));
        checkBox1 = new JCheckBox();
        checkBox1.setText("");
        panel2.add(checkBox1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        editButton = new JButton();
        editButton.setText("Edit");
        panel2.add(editButton, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 15), new Dimension(-1, 15), new Dimension(-1, 15), 0, false));
        label1 = new JLabel();
        label1.setFont(new Font(label1.getFont().getName(), label1.getFont().getStyle(), 18));
        label1.setText("Label");
        panel1.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 20), new Dimension(-1, 20), new Dimension(-1, 20), 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(391, 320), new Dimension(391, 320), new Dimension(391, 320), 0, false));
        checkBoxList = new CheckBoxList();
        scrollPane1.setViewportView(checkBoxList);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
