package ru.lanit.dibr.utils.core;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: Vova
 * Date: 13.11.12
 * Time: 2:12
 */
public class SimpleLocalFileSource implements LogSource {

    private boolean isClosed = false;
    private AtomicBoolean paused = new AtomicBoolean(false);
    SynchronousQueue<String> readQueue = new SynchronousQueue<String>();
    private long SLEEP = 5;
    private boolean writeLineNumbers = false;

    private File fileToRead;
    int writedLines = 0;
    BufferedReader reader = null;

    public SimpleLocalFileSource(String filename, long sleep) {
        this(filename);
        SLEEP = sleep;
    }
    public SimpleLocalFileSource(String filename) {
        fileToRead = new File(filename);
        if(!fileToRead.exists() || !fileToRead.isFile() || !fileToRead.canRead()) {
            throw new RuntimeException("Не могу открыть файл!");
        }
    }

    public void startRead() throws Exception {
        checkClosed();

        reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToRead)));

        Thread readThread = new Thread(new Runnable() {
            public void run() {
                String nextLine;
                try {
                    System.out.println("Start read file \"" + fileToRead + "\"");
                    while ((nextLine = reader.readLine()) != null && !isClosed) {
                        while (paused.get() && !isClosed) {
                            Thread.sleep(100);
                        }
                        if(writeLineNumbers) {
                            readQueue.put(String.format("%6d: %s", (++writedLines), nextLine));
                        } else {
                            readQueue.put(nextLine);
                        }

                        if(SLEEP > 0) {
                            Thread.sleep(SLEEP);
                        }
                    }
                    System.out.println("Finish read file \"" + fileToRead + "\"");
                } catch (IOException e) {
                    try {
                        close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        readThread.start();

    }

    private void checkClosed() {
        if (isClosed) {
            throw new RuntimeException("Reader is closed");
        }
    }

    public String readLine() throws IOException {
        try {
            while (paused.get() && !isClosed) {
                System.out.println("I'm asleep.. ( " + fileToRead + " )");
                Thread.sleep(100);
            }
            if(isClosed) {
                throw new IOException("Source is closed!");
            }
            String str = readQueue.poll(100, TimeUnit.MILLISECONDS);
            return str!=null?str:LogSource.SingletonSkipLineValue.SKIP_LINE;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return LogSource.SingletonSkipLineValue.SKIP_LINE;
    }

    public void reset() {
        paused.set(true);
        writedLines = 0;
        try {
            freeResources();
            readQueue.clear();
            startRead();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        paused.set(false);
    }

    public void reloadFull() throws Exception {
        reset();
    }

    public void close() throws Exception {
        isClosed = true;
        freeResources();
    }

    private void freeResources() throws Exception {
        if (reader != null)
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public String getName() {
        return "Test Source";
    }

    @Override
    public boolean isWriteLineNumbers() {
        return writeLineNumbers;
    }

    @Override
    public BlockingQueue<String> getDebugOutput() {
        return debugOutput;
    }

    public void setPaused(boolean paused) {
        System.out.println("set paused: " + paused);
        this.paused.set(paused);
    }

    public boolean isPaused() {
        return paused.get();
    }
}
