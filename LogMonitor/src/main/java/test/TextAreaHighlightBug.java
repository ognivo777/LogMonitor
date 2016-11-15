package test;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Vladimir
 * Date: 19.05.15
 * Time: 18:21
 * To change this template use File | Settings | File Templates.
 */
public class TextAreaHighlightBug {
    public static void main(String[] args) throws BadLocationException {
        JTextArea area = new JTextArea();
        JScrollPane pane = new JScrollPane(area);
        JTextArea area2 = ((JTextArea) pane.getViewport().getView());
        area2.append("123456");
        Object o =area2.getHighlighter().addHighlight(3, 6, new DefaultHighlighter.DefaultHighlightPainter(Color.GRAY));
        area2.append("\n123456");
        area2.getHighlighter().removeHighlight(o);
        area2.getHighlighter().addHighlight(9, 10, new DefaultHighlighter.DefaultHighlightPainter(Color.GRAY));
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(new Dimension(200, 200));
        jFrame.setContentPane(pane);
        jFrame.setVisible(true);
    }
}
