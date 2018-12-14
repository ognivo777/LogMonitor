package hlam.customContent;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.undo.UndoableEdit;

/**
 * Created by Vova on 24.12.2015.
 */
public class StringBuilderContent implements AbstractDocument.Content {
    public StringBuilderContent(StringBuilder stringBuilder) {

    }

    @Override
    public Position createPosition(int offset) throws BadLocationException {
        return null;
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public UndoableEdit insertString(int where, String str) throws BadLocationException {
        return null;
    }

    @Override
    public UndoableEdit remove(int where, int nitems) throws BadLocationException {
        return null;
    }

    @Override
    public String getString(int where, int len) throws BadLocationException {
        return null;
    }

    @Override
    public void getChars(int where, int len, Segment txt) throws BadLocationException {

    }
}
