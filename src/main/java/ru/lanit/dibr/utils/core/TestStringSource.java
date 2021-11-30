package ru.lanit.dibr.utils.core;

import ru.lanit.dibr.utils.core.LogSource;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * User: Vova
 * Date: 13.11.12
 * Time: 2:12
 */
public class TestStringSource implements LogSource {

    private boolean isClosed = false;
    private boolean paused = false;
    private int delay = 100;
    private boolean showLineNums = true;
    List<String> buffer = new ArrayList<String>();

    private List<String> strings;
    int readedLines = 0;
    private int readedLinesFromStrings = 0;

    public TestStringSource(String data) {
        strings = Arrays.asList(data.split("\n"));
    }

    public TestStringSource(String data, int delay, boolean showLineNums) {
        strings = new ArrayList<String>();
        strings.addAll(Arrays.asList(data.split("\n")));
        strings.add("\n");
        strings.add("\n");
        this.delay = delay;
        this.showLineNums = showLineNums;
    }

    public void startRead() throws Exception {
        checkClosed();
        Thread readThread = new Thread(new Runnable() {
            public void run() {
                String nextLine;
                try {
                    while ((nextLine = readNextLine()) != null && !isClosed) {
                        Thread.sleep(delay);
                        if(showLineNums)
                            buffer.add(String.format("%6d: %s", (buffer.size()+1), nextLine));
                        else
                            buffer.add(nextLine);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        readThread.start();

    }

    private String readNextLine() {
        if(readedLinesFromStrings==strings.size()) {
            isClosed = true;
            return LogSource.SingletonSkipLineValue.SKIP_LINE;
        }
        return strings.get(readedLinesFromStrings++);
    }

    private void checkClosed() {
        if (isClosed) {
            throw new RuntimeException("Reader is closed");
        }
    }

    public String readLine() throws IOException {
        try {
            while (paused) {
                System.out.println("I'm asleep..");
                Thread.sleep(50);
            }
            if (buffer.size() > readedLines) {
                return buffer.get(readedLines++);
            } else {
                Thread.sleep(50);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return LogSource.SingletonSkipLineValue.SKIP_LINE;
    }

    public void reset() {
        readedLines = 0;
        //reader.reset();
    }

    public void reloadFull() throws Exception {
        //todo
    }

    public void close() throws Exception {
        isClosed = true;
    }

    @Override
    public String getName() {
        return "TestStringSource";
    }

    @Override
    public boolean isWriteLineNumbers() {
        return false;
    }

    @Override
    public BlockingQueue<String> getDebugOutput() {
        return debugOutput;
    }

    public void setPaused(boolean paused) {
        System.out.println("set paused: " + paused);
        this.paused = paused;
    }

    public boolean isPaused() {
        return paused;
    }
}
