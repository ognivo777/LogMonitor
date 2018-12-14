package hlam.customContent;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * Created by Vova on 24.12.2015.
 */
public class MainJTextAreaWithCustomContent {
    public static void main(String[] args) {

        AbstractDocument.Content c = new StringBuilderContent(new StringBuilder(10));
        Document d = new PlainDocument(c);
        JTextArea jt = new JTextArea(d);
    }
}
