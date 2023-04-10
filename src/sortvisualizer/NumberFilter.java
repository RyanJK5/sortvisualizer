package sortvisualizer;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class NumberFilter extends DocumentFilter {
    public final float max;
    public final boolean checkForFloat;

    public NumberFilter(float max, boolean checkForFloat) {
        this.max = max;
        this.checkForFloat = checkForFloat;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        String docStr = fb.getDocument().getText(0, fb.getDocument().getLength());
        docStr = docStr.substring(0, offset) + string + docStr.substring(offset);
        if (runCheck(docStr)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
        String docStr = fb.getDocument().getText(0, fb.getDocument().getLength());
        docStr = docStr.substring(0, offset) + string + docStr.substring(offset + length);
        if (runCheck(docStr)) {
            super.replace(fb, offset, length, string, attr);
        }
    }

    private boolean runCheck(String str) {
        return checkForFloat ? isFloat(str) : isInt(str);
    }

    private boolean isFloat(String str) {
        try {
            return Float.parseFloat(str) <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isInt(String str) {
        try {
            return Integer.parseInt(str) <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
