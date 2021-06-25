package ru.lanit.dibr.utils.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import ru.lanit.dibr.utils.CmdLineConfiguration;
import ru.lanit.dibr.utils.core.*;
import ru.lanit.dibr.utils.gui.forms.Filters;
import ru.lanit.dibr.utils.utils.Utils;

import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * User: VTaran
 * Date: 16.08.2010
 * Time: 15:56:23
 */
public class LogPanel extends JScrollPane implements KeyListener, CaretListener, MouseListener {
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JScrollPane.class);

    private LogSource logSource;
    private Source filtersChain;
    private String blockPattern;
    private boolean stopped = false;
    private JTextArea area;
    private AtomicBoolean autoScroll = new AtomicBoolean(true);
    private String find = null;


    private SearchFilter grepInvertedFilter = new LineSearchFilter(true);
    private SearchFilter grepDirectFilter = new LineSearchFilter(false);
    private SearchFilter blockInvertedFilter;
    private SearchFilter blockDirectFilter;

    private Filter additionalFilter = new NoFilterImpl();

    private int startFrom = 0;
    private int offset = 0;
    boolean lastSearchDirectionIsForward = true;

    private boolean isClosed = false;

    final Highlighter hilit;
    final Highlighter.HighlightPainter painter;

    private int linesCount = 0;

    AtomicBoolean mouseClickedOnScrollBar = new AtomicBoolean(false);

    public Filters filtersWindow;

    public LogPanel(LogSource logSource, String blockPattern){
        this(logSource, blockPattern, new Theme());
    }

    public LogPanel(LogSource logSource, String blockPattern, Theme theme) {
        super(new JTextArea());
        this.logSource = logSource;
        this.blockPattern = blockPattern;

        grepInvertedFilter = new LineSearchFilter(true);
        grepDirectFilter = new LineSearchFilter(false);

        blockInvertedFilter = new BlockSearchFilter(blockPattern, true);
        blockDirectFilter = new BlockSearchFilter(blockPattern, false);

        filtersWindow = new Filters("Filters", this, blockDirectFilter, blockInvertedFilter, grepDirectFilter, grepInvertedFilter);

        area = ((JTextArea) getViewport().getView());

        area.setEditable(false);
        area.setFont(new Font("Courier New", 1, CmdLineConfiguration.fontSize));
//        area.setBackground(new Color(0, 0, 0));
        area.setBackground(theme.backgroundColor);
//        area.setForeground(new Color(187, 187, 187));
        area.setForeground(theme.textColor);
//        area.setSelectedTextColor(new Color(0, 0, 0));
        area.setSelectedTextColor(theme.textSelectedColor);
        area.setSelectionColor(theme.backgroundSelectedColor);
//        area.setSelectionColor(new Color(187, 187, 187));
        area.addMouseListener(this);

        area.setWrapStyleWord(true);
        area.setLineWrap(true);

        hilit = new DefaultHighlighter();
        painter = new DefaultHighlighter.DefaultHighlightPainter(Color.GRAY);
        area.setHighlighter(hilit);

        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
//        System.out.println(getVerticalScrollBar().getPreferredSize().toString());
        getVerticalScrollBar().setPreferredSize(new Dimension((int) Math.round(CmdLineConfiguration.fontSize*1.25), 0));


        getVerticalScrollBar().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseClickedOnScrollBar.set(true);
                System.out.println("mouseClickedOnScrollBar.get() = " + mouseClickedOnScrollBar.get());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseClickedOnScrollBar.set(false);
                System.out.println("mouseClickedOnScrollBar.get() = " + mouseClickedOnScrollBar.get());
            }
        });
        getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if(mouseClickedOnScrollBar.get()) {
                    System.out.println("ScrollBar drag event");
                    int extent = getVerticalScrollBar().getModel().getExtent();
                    int pos = getVerticalScrollBar().getValue() + extent;
                    int maxPos = getVerticalScrollBar().getMaximum();
                    System.out.println("Value: " + pos + " Max: " + maxPos);
                    if(pos!=maxPos && autoScroll.get()){
                        setAutoScroll(false);
                    }
                    if(pos==maxPos && !autoScroll.get()) {
                        setAutoScroll(true);
                    }
                }
            }
        });

        setAutoScroll(true);
    }

    public void connect() throws Exception {
        try {
            final BlockingQueue<String> dbg = logSource.getDebugOutput();

            final AtomicBoolean stopReadDbg = new AtomicBoolean(false);
            Thread dbgRead = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(!stopReadDbg.get()) {
                        try {
                            String msg = dbg.poll();
                            if(msg!=null) {
                                appendLine(msg);
                                continue;
                            }
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            });

            dbgRead.start();
            logSource.startRead();
            Thread.sleep(300);
            stopReadDbg.set(true);
            dbgRead.interrupt();
//            dbgRead.join();

            filtersChain = logSource;
            String nextLine;
            area.addKeyListener(this);
            area.addCaretListener(this);

            new Thread(new Runnable() {
                public void run() {
                    while (!isClosed) {
                        try {
//                            int cnt = 0;
//                            if (autoScroll) {
//                                getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
//                            }
                            area.repaint();
                            /*if (needRepaint.getAndSet(false) || (cnt++) == 8) {
                                cnt = 0;
                                getParent().repaint(0);
                                repaint(0);
                            }*/
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            }, "Repaint").start();

            this.addMouseWheelListener(new MouseAdapter() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    //System.out.println(e);
                    setAutoScroll(!getVerticalScrollBar().isVisible() || (getVerticalScrollBar().getValue() + getVerticalScrollBar().getHeight()) == getVerticalScrollBar().getMaximum());
                }
            });

            appendLine("============ LOG STARTS AFTER THIS LINE ============");

            StringBuffer buff = new StringBuffer();
            while (!stopped && !isClosed) {
                buff.setLength(0);
                while (!(nextLine = filtersChain.readLine()).equals(LogSource.SingletonSkipLineValue.SKIP_LINE) ) {
                    buff.append(nextLine+"\n");
                }
                if(buff.length()>0) {
                    appendLine(buff.substring(0, buff.length()-1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            logSource.close();
        }
    }

    private void appendLine(final String nextLine) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                int pos = area.getText().length()-1;
                linesCount += (nextLine.split("\n").length + 1);
                if(linesCount >= logSource.MAX_CACHED_LINES) {
                    int i = 0;
                    int j = 0;
                    for (; j < 100; j++) {
                        i = area.getText().indexOf("\n", i + 1);
                        if(i<0) {
                            break;
                        }
                    }
                    if(i > 1) {
                        log.info("Crop upper lines: " + j + ". (up to pos: " + i + ")");
                        linesCount-=j;
                        area.replaceRange("", 0, i + 1);
                    }
                }
                area.append("\n" + nextLine);
                if(find!=null && find.length()>0) {
                    highlightFromCursor(hilit, painter, pos);
                }
                System.out.println("Get autoscroll as " + (autoScroll+ "").toUpperCase() );
                if (autoScroll.get()) {
                    getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
                }
            }
        });
    }

    public void close() {
        if(isClosed) {
            System.out.println("Log Panel already closed!");
            return;
        }
        System.out.println("Closing log panel..");
        isClosed = true;
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                clearView();
//                area.setText("");
            }
        });
        try {
            logSource.close();
        } catch (Exception e) {
            System.out.println("Can't close log source:");
            e.printStackTrace();
        }
    }

    private void clearView() {
        area.replaceRange("",0,area.getText().length());
        linesCount = 0;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void follow() {
        setAutoScroll(true);
        getVerticalScrollBar().setValue(getVerticalScrollBar().getMaximum());
    }

    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll.set(autoScroll);
        System.out.println("Set autoscroll to " + (autoScroll+ "").toUpperCase() );
        if(autoScroll) {
            getVerticalScrollBar().setBorder(new LineBorder(Color.GREEN,2));
        }
        else {
            if(area.getCaretPosition()>0 && area.getCaretPosition() == area.getText().length())
                area.setCaretPosition(area.getCaretPosition()-1);
            getVerticalScrollBar().setBorder(null);
        }
    }

    public void keyPressed(KeyEvent ke) {

        if (ke.getModifiers() == 0) {
            if ((ke.getKeyCode() == 27)) { //Нажали PgUp
                //TODO fix: tight coupling with parent Frame (LogFrame)
                Container container = getParent();
                while (!JFrame.class.isAssignableFrom(container.getClass())) {
                    container = container.getParent();
                }
                WindowEvent closingEvent = new WindowEvent((Window) container, WindowEvent.WINDOW_CLOSING);
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closingEvent);

            } else if ((ke.getKeyCode() == KeyEvent.VK_PAGE_UP)) { //Нажали PgUp
                setAutoScroll(false);
            } else if ((ke.getKeyCode() == KeyEvent.VK_W)) { //Нажали W - перенос строк
                area.setLineWrap(!area.getLineWrap());
            } else if ((ke.getKeyCode() == KeyEvent.VK_P)) { //Нажали P - вывод текущего лога в попапе
                try {
                    new PopupBlock("Log snapshot",area.getText(), false);
                } catch (Exception e) {
                    System.out.println("Error while open current log snapshot in popup window.");
                    e.printStackTrace();
                }
            } else if ((ke.getKeyCode() == KeyEvent.VK_N)) { //Нажали N - номера строк
                logSource.setPaused(true);
//                area.setText("");
//                linesCount = 0;
                clearView();
                setAutoScroll(true);
                logSource.setWriteLineNumbers(!logSource.isWriteLineNumbers());
                logSource.reset();
                logSource.setPaused(false);
            } else if (ke.getKeyCode() == KeyEvent.VK_F1) { // F1
                new HotKeysInfo();
            } else if (ke.getKeyCode() == KeyEvent.VK_C) { // Key 'C'
                logSource.setPaused(true);
//                area.setText("");
                clearView();
                logSource.setPaused(false);
            }
        }
        if(ke.getModifiers() == 0 || ke.getModifiers() == KeyEvent.SHIFT_MASK) {
            if (ke.getKeyCode() == KeyEvent.VK_F3) { // F3 (+Shift)
                findWord(ke.getModifiers() == KeyEvent.SHIFT_MASK);
            } else if (ke.getKeyCode() == KeyEvent.VK_F5) { // [Shift +] F5
                if (ke.getModifiers() == KeyEvent.SHIFT_MASK) {
                    clearFilters();
                } else {
                    resetFilters();
                }
            }
        } else if ((ke.getModifiers() == KeyEvent.CTRL_MASK ) || ( ke.getModifiers() == (KeyEvent.SHIFT_MASK | KeyEvent.CTRL_MASK))) {
            boolean isOnlyShiftAdditionalPressed = (ke.getModifiers()&KeyEvent.SHIFT_MASK) != 0;
            if(!isOnlyShiftAdditionalPressed) {
                if ((ke.getKeyCode() == KeyEvent.VK_HOME)) { //Нажали Ctrl + Home
                    setAutoScroll(false);
                    area.setCaretPosition(0);
                } else if ((ke.getKeyCode() == KeyEvent.VK_END)) { //Нажали Ctrl + End
                    setAutoScroll(true);
                } else if (ke.getKeyCode() == KeyEvent.VK_S ) { // Key Ctrl + 'S'
                    logSource.setPaused(true);
                    findSimilar();
                    logSource.setPaused(false);

                }
            }
            if (ke.getKeyCode() == KeyEvent.VK_F) {  //  Нажали Ctrl [+Shift] + F
                performFind(isOnlyShiftAdditionalPressed);
            } else if (ke.getKeyCode() == KeyEvent.VK_G) {  // GREP filter
                addGrepFilter(isOnlyShiftAdditionalPressed);
            } else if ((ke.getKeyCode() == KeyEvent.VK_B) && blockPattern != null ) {  //BLOCK filter
                //ToDo: если blockPattern==null - Сообщить об этом и предложить его ввести. Показав что нибудь по дефолту. А потом сделать визард с проверкой по строке из лога.
                addBlockFilter(isOnlyShiftAdditionalPressed);
            }
        } else {
            System.out.println(ke.getKeyCode());
        }
    }

    public void addBlockFilter(boolean inverseBlock) {
        addFilter(inverseBlock ? blockInvertedFilter : blockDirectFilter, "Block filter");
    }

    public void addGrepFilter(boolean inverseGrep) {
        addFilter(inverseGrep ? grepInvertedFilter : grepDirectFilter, "Grep filter");
    }

    private void addFilter(SearchFilter filter, String title) {
        String pattern = (String) JOptionPane.showInputDialog(this, title + ":\n", title, JOptionPane.INFORMATION_MESSAGE, null, null, null);
        System.out.println(title + " entered: '" + pattern + "'");

        if (pattern.isEmpty()) {
            filter.disable();
        } else {
            filter.addStringToSearch(pattern);
        }
        filtersWindow.addTo(filter);
        resetFilters();
    }

    public void performFind(boolean isBackWard) {
        find = (String) JOptionPane.showInputDialog(this, "FIND:\n", "Find", JOptionPane.INFORMATION_MESSAGE, null, null, null);
        startFrom = area.getCaretPosition();
        if (highlightFound() > 0)
            findWord(isBackWard);
    }

    public void clearFilters() {
        blockInvertedFilter.disable();
        blockDirectFilter.disable();
        grepInvertedFilter.disable();
        grepDirectFilter.disable();
        additionalFilter.disable();
        resetFilters();
    }

    public void resetFilters() {
        logSource.setPaused(true);
        filtersChain = logSource;
//        area.setText("");
        clearView();
        if (blockInvertedFilter.isActive()) {
            filtersChain = blockInvertedFilter.apply(filtersChain);
        }
        if (blockDirectFilter.isActive()) {
            filtersChain = blockDirectFilter.apply(filtersChain);
        }
        if (grepInvertedFilter.isActive()) {
            filtersChain = grepInvertedFilter.apply(filtersChain);
        }
        if (grepDirectFilter.isActive()) {
            filtersChain = grepDirectFilter.apply(filtersChain);
        }
        if (additionalFilter.isActive()) {
            filtersChain = additionalFilter.apply(filtersChain);
        }
        logSource.reset();
        logSource.setPaused(false);

        setAutoScroll(true);
    }

    public boolean findWord(boolean isBackWard) {
        setAutoScroll(false);
        if (lastSearchDirectionIsForward == isBackWard) {
            lastSearchDirectionIsForward = !isBackWard;
            if (!findWord(isBackWard))
                return false;
        } else {
            lastSearchDirectionIsForward = !isBackWard;
        }
        for (int i = 0; i < 2; i++) {
            if (isBackWard) {
                //offset = area.getText().lastIndexOf(find, startFrom);
                offset = Utils.lastIndexOf(area.getText(), false, startFrom, find);
            } else {
//                offset = area.getText().indexOf(find, startFrom);
                offset = Utils.indexOf(area.getText(), false, startFrom, find);
             }
            if (offset > -1) {
                area.setFocusCycleRoot(true);
                area.requestFocusInWindow();
//                area.select(offset, find.length() + offset);
                area.setCaretPosition(offset + find.length());
                area.moveCaretPosition(offset);
                startFrom = offset + (isBackWard ? -1 : (find.length() + 1));
                return true;
            }
            startFrom = isBackWard ? (area.getText().length() - 1) : 0;
        }
        JOptionPane.showMessageDialog(this, "No matches found!");
        return false;
    }

    public int highlightFound() {
        setAutoScroll(false);
        hilit.removeAllHighlights();
        int pos = 0;
        int cnt = highlightFromCursor(hilit, painter, pos);
        JOptionPane.showMessageDialog(this, cnt + " matches found");
        return cnt;
    }

    Object prevHighlightForRecreate = null;
    int p0, p1;
    private int highlightFromCursor(Highlighter h, Highlighter.HighlightPainter p, int pos) {
        System.out.println("pos: " + pos);
        logSource.setPaused(true);
        int cnt = 0;
        try {
            if(prevHighlightForRecreate!=null) {
                System.out.println("Remove prev");
                h.removeHighlight(prevHighlightForRecreate);
                try {
                    h.addHighlight(p0, p1, p);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                prevHighlightForRecreate = null;
            }

            while (pos >= 0) {
                pos = Utils.indexOf(area.getText(), false, pos, find);
                System.out.println("pos: " + pos);
                if (pos > -1) {
                    try {
                        cnt++;
                        int endPos = find.length() + pos;

                        System.out.println("Highlight: " + pos + ":" + endPos + ". Area.size():" + area.getText().length());
                        System.out.println("Doc positions: " + area.getDocument().createPosition(pos) + ":" + area.getDocument().createPosition(endPos));

                        //area.repaint();
                        Object o = h.addHighlight(pos, endPos, p);
                        if(endPos >= area.getText().length()) {
                            System.out.println("To be removed");
                            prevHighlightForRecreate = o;
                            p0 = pos;
                            p1 = endPos;
                        }
                        pos = endPos;
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            logSource.setPaused(false);
        }
        return cnt;
    }


    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void caretUpdate(CaretEvent e) {
        startFrom = e.getDot();
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            try {
                int ss = area.getSelectionStart();
                ss = area.getText().lastIndexOf("\n", ss) + 1;
                int se = area.getText().indexOf("\n", ss);
                System.out.println("DblClk! \n" + area.getText(ss, se - ss));
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }

        }
        if (e.getClickCount() == 2 && blockPattern != null) {
            try {

                String block = findBlockAroundCursor();

                if (block != null) {
                    int firstLineOfBlockEndPos = block.indexOf('\n');
                    if (firstLineOfBlockEndPos < 0) {
                        firstLineOfBlockEndPos = block.length();
                    }
                    String title = block.substring(0, firstLineOfBlockEndPos);
                    new PopupBlock(title, block, true);
                }

            } catch (PatternSyntaxException ex) {
                JOptionPane.showMessageDialog(this, "Block pattern is wrong!");
            } catch (Exception e1) {
                System.out.println("Can't create block popup window!");
                e1.printStackTrace();
            } finally {
                logSource.setPaused(false);
            }

        }
    }

    private String findBlockAroundCursor() {
        setAutoScroll(false);
        logSource.setPaused(true);

        Pattern compiledBlockPattern = getBlockPatern();
        boolean isBeginFound = false;
        boolean isEndFound = false;
        int firstStartPos, firstEndPos;
        int secondStartPos, secondEndPos;
        int cursorPos = area.getCaretPosition() - 1;
        if (area.getText().charAt(cursorPos) == '\n') {
            cursorPos--;
        }
        firstStartPos = secondStartPos = cursorPos;
        String first, second;
        String block = null;
        for (int i = 0; i < 500; i++) {
            if (!isBeginFound) {
                firstStartPos = area.getText().lastIndexOf("\n", firstStartPos) + 1;
                firstEndPos = area.getText().indexOf("\n", firstStartPos);
                System.out.println("firstStartPos = " + firstStartPos);
                System.out.println("firstEndPos = " + firstEndPos);
                if (firstStartPos < 0) {
                    System.out.println("Begin detected. Use begining of log.");
                    firstStartPos = 0;
                    isBeginFound = true;
                } else {
                    if (firstEndPos < 0) {
                        System.out.println("End detected. Use end of log.");
                        firstEndPos = secondStartPos = area.getText().length();
                        isEndFound = true;
                    }
                    first = area.getText().substring(firstStartPos, firstEndPos);
                    System.out.println("first = " + first);
                    if (compiledBlockPattern.matcher(first).matches()) {
                        System.out.println("start line found: \"" + first + "\"");
                        isBeginFound = true;
                    } else {
                        firstStartPos = firstStartPos - 2;
                    }
                }
            }

            if (!isEndFound) {
                secondStartPos = area.getText().indexOf("\n", secondStartPos) + 1;
                secondEndPos = area.getText().indexOf("\n", secondStartPos);
                System.out.println("secondStartPos = " + secondStartPos);
                System.out.println("secondEndPos = " + secondEndPos);
                if (secondEndPos < 0 || secondStartPos < 0) {
                    System.out.println("End detected. Use end of log.");
                    isEndFound = true;
                    secondStartPos = area.getText().length();
                } else if (secondStartPos == area.getText().length()) {
                    isEndFound = true;
                } else if (secondEndPos != secondStartPos) {
                    second = area.getText().substring(secondStartPos, secondEndPos);
                    System.out.println("second = " + second);
                    if (compiledBlockPattern.matcher(second).matches()) {
                        System.out.println("second line found: \"" + second + "\"");
                        isEndFound = true;
                    } else {
                        secondStartPos = secondEndPos;
                    }
                }
            }

            if (isBeginFound && isEndFound) {
                block = area.getText().substring(firstStartPos, secondStartPos);
                System.out.println("found block: " + block);
                break;
            }

        }
        logSource.setPaused(false);
        return block;
    }

    private Pattern getBlockPatern() {
        String patternPrefix = "";
        if (logSource instanceof SshSource && ((SshSource) logSource).isWriteLineNumbers()) {
            patternPrefix = "\\s*\\d{1,6}: ";
        }
        return Pattern.compile(patternPrefix + this.blockPattern + ".*");
    }


    public void findSimilar() {
        System.out.println("LeviDistance\tBlock Size1\tBlock size2");
        Pattern compiledBlockPattern = getBlockPatern();
        String blockAroundCursor = findBlockAroundCursor();
        String log = area.getText();
        int b0size = blockAroundCursor.length();
        int lineStart = 0;
        int curBlockStartPos = 0;
        int lineEnd = 0;
        StringBuffer blockBuffer = new StringBuffer();
        StringBuffer result = new StringBuffer();
        while ((lineEnd = log.indexOf('\n', lineStart)) >= 0) {
            String line = log.substring(lineStart, lineEnd);
            if (compiledBlockPattern.matcher(line).matches()) {
                int b1size = blockBuffer.length();
                double m1 = (b0size + b1size + 100) / (Math.abs(b0size - b1size + 0.0));
                if (m1 > 10) { //Разница размеров блоков не менее чем в 10 раз меньше суммы
                    int distance = Utils.getLevenshteinDistance(blockAroundCursor, blockBuffer.toString(), 1000);
                    double m2 = Math.abs((b0size - 100) / (distance + 0.0));
                    if (m2 > 5) {
                        result.append("--------------------------------------------------------------------------------------------------------------------------------\n");
                        //result.append(distance + "\t" + blockAroundCursor.length() + "\t" + blockBuffer.length() + "\t" + m1 + "\t" + m2);
                        result.append(blockBuffer).append("\n");
                    }
                }
                blockBuffer.setLength(0);
                curBlockStartPos = lineEnd;
            }
            blockBuffer.append(line).append("\n");
            lineStart = lineEnd + 1;
        }

        System.out.println(result);
        try {
            new PopupBlock("Similar blocks", result.toString(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
        if (area.getSelectedText() != null) {
            String selected = area.getSelectedText();
            if (logSource.isWriteLineNumbers()) {
                selected = removeLineNumbers(selected);
            }
            setAutoScroll(false);
            StringSelection ss = new StringSelection(selected);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
        }
    }

    private String removeLineNumbers(String selected) {
        selected = selected.replaceAll("^[\\s\\d]*:\\s", "");
        selected = selected.replaceAll("\n[\\s\\d]*:\\s", "\n");
        return selected;
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void addAreaKyeListner(KeyListener kl) {
        area.addKeyListener(kl);
    }

    public Filter getAdditionalFilter() {
        return additionalFilter;
    }

    public void setAdditionalFilter(Filter additionalFilter) {
        this.additionalFilter = additionalFilter;
    }

    public String getLogSourceName() {
        return logSource.getName();
    }
}
