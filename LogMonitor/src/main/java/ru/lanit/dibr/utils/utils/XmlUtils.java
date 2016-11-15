package ru.lanit.dibr.utils.utils;

/**
 * User: Vova
 * Date: 16.11.12
 * Time: 0:43
 */
public class XmlUtils {
    private static XmlFormatter formatter = new XmlFormatter(2);

    public static String formatXml(String s) {
        return formatter.format(s, 0);
    }

    public static String formatXml(String s, int initialIndent) {
        return formatter.format(s, initialIndent);
    }

    private static class XmlFormatter {
        private int indentNumChars;
        private boolean singleLine;
        private boolean isFirstTag = true;

        public XmlFormatter(int indentNumChars) {
            this.indentNumChars = indentNumChars;
        }

        public synchronized String format(String s, int initialIndent) {
            s =  s.replaceAll(">\\s+<", "><");
            int indent = initialIndent;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char currentChar = s.charAt(i);
                if (currentChar == '<') {
                    char nextChar = s.charAt(i + 1);
                    if (nextChar == '/')
                        indent -= indentNumChars;
                    if (!singleLine) {  // Don't indent before closing element if we're creating opening and closing elements on a single line.
                        if(isFirstTag) {
                            isFirstTag = false;
                            sb.append("\n");
                        }
                        sb.append(buildWhitespace(indent));
                    }
                    if (nextChar != '?' && nextChar != '!' && nextChar != '/')
                        indent += indentNumChars;
                    singleLine = false;  // Reset flag.
                }
                sb.append(currentChar);
                if (currentChar == '>') {
                    if (s.charAt(i - 1) == '/') {
                        indent -= indentNumChars;
                        sb.append("\n");
                    } else {
                        int nextStartElementPos = s.indexOf('<', i);
                        if (nextStartElementPos > i + 1) {
                            String textBetweenElements = s.substring(i + 1, nextStartElementPos);

                            // If the space between elements is solely newlines, let them through to preserve additional newlines in source document.
                            if (textBetweenElements.replaceAll("\n", "").length() == 0) {
                                sb.append(textBetweenElements + "\n");
                            }
                            sb.append(textBetweenElements);
                            singleLine = true;
                            i = nextStartElementPos - 1;
                        } else {
                            sb.append("\n");
                        }
                    }
                }
            }
            return sb.toString();
        }
    }

    private static String buildWhitespace(int numChars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numChars; i++)
            sb.append(" ");
        return sb.toString();
    }

}
