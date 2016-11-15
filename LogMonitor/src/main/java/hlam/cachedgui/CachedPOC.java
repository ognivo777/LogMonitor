package hlam.cachedgui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.*;

/**
 * Created by Vova on 06.05.14.
 */
public class CachedPOC {
    private JPanel panel1;
    private JTextArea textArea1;
    private JScrollPane scrollPane;
    private JScrollBar scrollBar1;
    private String fileName;

    //    private BufferedReader reader;
    private FileInputStream inputStreamReader;
    private RandomAccessFile randomAccessFile;

    final static int MAX_STRINGS = 10485760 / 4;
    private int[] linesStarts = new int[MAX_STRINGS];
    private int[] linesLens = new int[MAX_STRINGS];
    private int linesStartsLength = 0;

    private int[] wrappedLinesStarts = new int[MAX_STRINGS * 3 / 2];
    private int[] wrappedLinesLens = new int[MAX_STRINGS * 3 / 2];
    private int wrappedLinesStartsLingth = 0;

    int currentFirstLine = 0;
    private int lrcnt;


    public CachedPOC(String fileName) throws IOException {
        this.fileName = fileName;
//        init();
        JFrame frame = new JFrame("tst");
        frame.add(panel1);
        //frame.setSize(1500, 1300);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public CachedPOC init() throws IOException {
//        panel1.add(textArea1);
        randomAccessFile = new RandomAccessFile(fileName, "r");


        return this;
    }


    /**
     * Сохраняем смещения в файле для первого символа и длину каждой строки.
     */
    private void analyseFile() throws IOException {
        inputStreamReader = new FileInputStream(fileName);
//        reader = new BufferedReader(inputStreamReader);
        int charVal;
        int prevCharVal = 0;
        int readedChars = 0;
        int lastLineEnd = 0;
        //TODO читать блоками по 8кб
        byte buf[] = new byte[8192];
        int readed = 0;
        int n = 0;
        while ((readed = inputStreamReader.read(buf)) >= 0) {
            for (int i = 0; i < readed; i++) {
                charVal = buf[i];
                readedChars++;
                if (prevCharVal == '\n' || charVal == '\r') {
                    continue;
                }
                if (charVal == '\n' || charVal == '\r') {
                    linesStarts[n] = lastLineEnd;
                    linesLens[n++] = readedChars - lastLineEnd;
                    lastLineEnd = readedChars;
                }
            }
        }

        linesStartsLength = n;
//        System.out.println("linesStarts = " + linesStarts.size());
    }

    /**
     * Создаём копию индексного массива, добавляем переносы строк что бы все строки вписались по ширине окна
     */
    private void wrapLinesToWindow(int lineLen) {
        int curLineLen;
        for (int i = 0; i < linesStartsLength; i++) {
            curLineLen = linesLens[i];
//            wrappedLinesStarts.add(linesStarts.get(i));
            wrappedLinesStarts[wrappedLinesStartsLingth++] = linesStarts[i];
            if (curLineLen > lineLen) {
//                wrappedLinesLens.add(lineLen);
                wrappedLinesLens[wrappedLinesStartsLingth] = lineLen;
            } else {
//                wrappedLinesLens.add(curLineLen);
                wrappedLinesLens[wrappedLinesStartsLingth] = curLineLen;
            }

            for (int wrappedLine = 0; wrappedLine < curLineLen / lineLen; wrappedLine++) {
//                wrappedLinesStarts.add(linesStarts.get(i) + (wrappedLine + 1) * lineLen);
                wrappedLinesStarts[wrappedLinesStartsLingth++] = linesStarts[i] + (wrappedLine + 1) * lineLen;
                if (lineLen * (wrappedLine + 2) < curLineLen) {
                    wrappedLinesLens[wrappedLinesStartsLingth] = lineLen;
                } else {
                    wrappedLinesLens[wrappedLinesStartsLingth] = curLineLen - lineLen * (wrappedLine + 1);
                }
            }
        }
        int freeLineStartsIdx = linesStarts.length - linesStartsLength;
        System.out.println("linesStarts = " + linesStartsLength + "(" + freeLineStartsIdx + " - " + 100 * linesStartsLength / linesStarts.length + "% used)");
        int freeWrappedLineStartsIdx = wrappedLinesStarts.length - wrappedLinesStartsLingth;
        System.out.println("wrappedLinesStarts = " + wrappedLinesStartsLingth + "(" + freeWrappedLineStartsIdx + " - " + 100 * wrappedLinesStartsLingth / wrappedLinesStarts.length + "% used)");
        scrollBar1.setMaximum(wrappedLinesStartsLingth - lrcnt);
//        System.out.println("wrappedLinesStarts = " + wrappedLinesStarts.size());
    }

    public void loadFile() throws IOException {
        System.out.println("START Analyse file");
        startTimer("Analyse file");
        analyseFile();
        stopTimer();
        System.out.println("FINISH Analyse file");
        textArea1.setFont(new Font("Courier new", 0, 12));
        // Ширина бкув
        int mcnt = textArea1.getWidth() / textArea1.getFontMetrics(textArea1.getFont()).charWidth('m');
        // высота букв
        lrcnt = textArea1.getHeight() / textArea1.getFontMetrics(textArea1.getFont()).getHeight();
        System.out.println("START Wrap lines");
        startTimer("Wrap lines");
        wrapLinesToWindow(mcnt);
        stopTimer();
        System.out.println("END Wrap lines");

        readWindow();
//        for (int j = 0; j < lrcnt; j++) {
//            for (int i = 0; i < mcnt; i++) {
//                text.append("m");
//            }
//            text.append('\n');
//        }


        textArea1.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getModifiers() == 0) {
                    if ((e.getKeyCode() == KeyEvent.VK_PAGE_UP)) {
                        currentFirstLine -= lrcnt;
                        readWindow();
                    } else if ((e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)) {
                        currentFirstLine += lrcnt;
                        readWindow();
                    }
                } else if (e.getModifiers() == KeyEvent.CTRL_MASK) {
                    if ((e.getKeyCode() == KeyEvent.VK_END)) {
                        currentFirstLine = wrappedLinesStartsLingth - lrcnt;
                        readWindow();
                    } else if ((e.getKeyCode() == KeyEvent.VK_HOME)) {
                        currentFirstLine = 0;
                        readWindow();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        scrollBar1.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
//                int extent = scrollBar1.getModel().getExtent();
                currentFirstLine = scrollBar1.getValue();
                //todo добавить проверку - если начало, то ставить первыюу строку, если конец - что бы внизу окна была последняя
                readWindow();
            }
        });

        startTimer("Find");
        System.out.println("Find: " + find("893,964,768"));
        stopTimer();
    }

    private void checkAndFixLineNum() {
        if (currentFirstLine < 0) {
            currentFirstLine = 0;
        } else if (currentFirstLine > wrappedLinesStartsLingth - lrcnt) {
            currentFirstLine = wrappedLinesStartsLingth - lrcnt;
        }
    }

    StringBuffer text = new StringBuffer();
    StringBuffer line = new StringBuffer();

    private void readWindow() {
        checkAndFixLineNum();
        scrollBar1.setValue(currentFirstLine);
        try {
            text.setLength(0);
            for (int j = currentFirstLine; j < currentFirstLine + lrcnt; j++) {
//                System.out.println("wrappedLinesStarts[" + j + "] = " + wrappedLinesStarts.get(j));
                randomAccessFile.seek(wrappedLinesStarts[j]);
                line.setLength(0);
//                System.out.println("wrappedLinesLens.get[" + j + "] = " + wrappedLinesLens.get(j));
                //todo чтать сразу нужное количество байт, а не по одному!
                for (int k = 0; k < wrappedLinesLens[j]; k++) {
                    line.append((char) randomAccessFile.readByte());
                }
                text.append(line.toString().replaceAll("[\n\r]", "")).append("\n");
            }
            textArea1.setText(text.substring(0, text.length() - 1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long find(String searchTerm) {
        try {
            randomAccessFile.seek(0);
            byte buff[] = new byte[8192];
            int rCnt = -1;
            long totalRaded = 0;
            while ((rCnt = randomAccessFile.read(buff)) >= 0) {
                //Todo учитывать что искомая подстрока могла быть на стыке читаемых блоков.
                String s = new String(buff, 0, rCnt, encoding);
                long pos = s.indexOf(searchTerm);
                if (pos >= 0) {
                    return totalRaded + pos;
                }
                totalRaded += rCnt;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }


    static long t;
    static String tName;
    static String encoding = "utf-8";

    public static void startTimer(String s) {
        tName = s;
        t = System.currentTimeMillis();
    }

    public static void stopTimer() {
        System.out.println("Timer: " + tName + ":\t" + (System.currentTimeMillis() - t) + "ms");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Used Memory: "
                + (runtime.totalMemory() - runtime.freeMemory()) / mb + "Mb");
        new CachedPOC("big.log").init().loadFile();
        while (true) {
            Thread.sleep(1000);
            System.out.println("Used Memory: "
                    + (runtime.totalMemory() - runtime.freeMemory()) / mb + "Mb");
        }
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
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(32);
        scrollPane.setVerticalScrollBarPolicy(22);
        scrollPane.putClientProperty("html.disable", Boolean.TRUE);
        panel1.add(scrollPane, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(1500, 900), new Dimension(1500, 900), new Dimension(1500, 900), 0, false));
        textArea1 = new JTextArea();
        textArea1.setLineWrap(false);
        textArea1.setWrapStyleWord(false);
        textArea1.putClientProperty("html.disable", Boolean.FALSE);
        scrollPane.setViewportView(textArea1);
        scrollBar1 = new JScrollBar();
        panel1.add(scrollBar1, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
