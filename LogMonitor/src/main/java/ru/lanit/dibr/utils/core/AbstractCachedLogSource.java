package ru.lanit.dibr.utils.core;

import ru.lanit.dibr.utils.gui.configuration.LogFile;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

public abstract class AbstractCachedLogSource implements LogSource {
    protected boolean isClosed = false;
    protected boolean paused;
    LinkedList<String> buffer;
    protected Iterator<String> descIter;
    String lastLine = null;
    protected LogFile logFile;


    protected AbstractCachedLogSource(LogFile logFile) {
        this.logFile = logFile;
        paused = false;
        buffer = new LinkedList();
    }

    public String readLine() throws IOException {
        try {
            while (paused && !isClosed) {
                System.out.println("I'm asleep..");
                Thread.sleep(10);
            }
            if(isClosed) {
                throw new IOException("Connection lost.");
            }
//            if (buffer.size() > readedLines) {
            int idx;
            if(descIter!=null) {
                if(descIter.hasNext()) {
                    lastLine = descIter.next();
                } else {
                    descIter = null;
                }
            } else if (!buffer.isEmpty()) {
                if (lastLine==null) {
                    descIter = buffer.descendingIterator();
                    lastLine = descIter.next();
                } else if ((idx=buffer.indexOf(lastLine))!=0) {
                    lastLine = buffer.get(idx-1);
                } else {
                    Thread.sleep(10);
                    return LogSource.SingletonSkipLineValue.SKIP_LINE;
                }
            } else {
                Thread.sleep(10);
                return LogSource.SingletonSkipLineValue.SKIP_LINE;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return lastLine;
    }

    public void setPaused(boolean paused) {
        System.out.println("set paused: " + paused);
        this.paused = paused;
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public BlockingQueue<String> getDebugOutput() {
        return debugOutput;
    }

    public void reset() {
        lastLine = null;
    }
}
