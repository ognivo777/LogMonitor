package ru.lanit.dibr.utils.gui.forms;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * User: Vova
 * Date: 12.11.13
 * Time: 2:10
 */
public class CheckBoxList extends JList {
    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
    private List<JCheckBox> items;

    public CheckBoxList() {
        setCellRenderer(new CellRenderer());

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());

                if (index != -1) {
                    JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
                    checkbox.setSelected(!checkbox.isSelected());
                    repaint();
                }
            }
        }
        );

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    protected class CellRenderer implements ListCellRenderer {
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JCheckBox checkbox = (JCheckBox) value;
            checkbox.setBackground(isSelected ?
                    getSelectionBackground() : getBackground());
            checkbox.setForeground(isSelected ?
                    getSelectionForeground() : getForeground());
            checkbox.setEnabled(isEnabled());
            checkbox.setFont(getFont());
            checkbox.setFocusPainted(false);
            checkbox.setBorderPainted(true);
            checkbox.setBorder(isSelected ?
                    UIManager.getBorder(
                            "List.focusCellHighlightBorder") : noFocusBorder);
            return checkbox;
        }
    }

    public void addCheckbox(JCheckBox checkBox) {
        ListModel currentList = this.getModel();
        JCheckBox[] newList = new JCheckBox[currentList.getSize() + 1];
        for (int i = 0; i < currentList.getSize(); i++) {
            newList[i] = (JCheckBox) currentList.getElementAt(i);
        }
        newList[newList.length - 1] = checkBox;
        setListData(newList);
    }

    public void markAll(boolean checked) {
        ListModel currentList = this.getModel();
        for (int i = 0; i < currentList.getSize(); i++) {
            ((JCheckBox) currentList.getElementAt(i)).setSelected(checked);
        }
        repaint();
    }

    public void removeCheckbox(int idx) {
        ListModel currentList = this.getModel();
        java.util.List<JCheckBox> newList = new ArrayList<JCheckBox>(currentList.getSize());
        for (int i = 0; i < currentList.getSize(); i++) {
            newList.add((JCheckBox) currentList.getElementAt(i));
        }
        newList.remove(idx).setSelected(false);
        setListData(newList.toArray());
    }

    public void clear() {
        ListModel currentList = this.getModel();
        for (int i = 0; i < currentList.getSize(); i++) {
            ((JCheckBox) currentList.getElementAt(i)).setSelected(false);
        }
        setListData(new JCheckBox[0]);
    }

}
