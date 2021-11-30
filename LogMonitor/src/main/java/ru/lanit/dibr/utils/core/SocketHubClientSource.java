package ru.lanit.dibr.utils.core;

import com.pega.apache.log4j.spi.LoggingEvent;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketHubClientSource implements LogSource {
    public static final String BLOCK_PATTERN = "\\[\\d{4}-\\d\\d?-\\d\\d? \\d\\d?:\\d\\d?:\\d\\d?[,:]\\d{1,3} MS[KD]\\]";
    private boolean isClosed = false;
    private AtomicBoolean paused = new AtomicBoolean(false);
    SynchronousQueue<String> readQueue = new SynchronousQueue<String>();
    private long SLEEP = 5;

    private AbstractHost host;
    private Socket as;
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS z");

    public SocketHubClientSource(AbstractHost host) {
        this.host = host;
    }

    public void startRead() throws Exception {
        checkClosed();

        as = new Socket(host.getHost(), host.getPort());

        Thread readThread = new Thread(new Runnable() {
            public void run() {
                try {
                    InputStream is = as.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(is);
                    StringBuilder stackTace = new StringBuilder();

                    while (!isClosed) {
                        while (paused.get() && !isClosed) {
                            Thread.sleep(20);
                        }

                        if(isClosed) {
                            break;
                        }

                        LoggingEvent e = (LoggingEvent) ois.readObject();
                        stackTace.setLength(0);

                        stackTace.append("[").append(df.format(new Date(e.timeStamp))).append("] ").append(e.getMessage());

                        String[] strRep = e.getThrowableStrRep();
                        if (strRep != null && strRep.length > 0) {
//                            stackTace.setLength(0);
                            for (int i = 0; i < strRep.length; i++) {
                                if (i > 0) {
                                    stackTace.append("\n");
                                }
                                stackTace.append(strRep[i]);
                            }
                            System.out.println(strRep);
                        }
                        readQueue.put(stackTace.toString());

                        if(SLEEP > 0) {
                            Thread.sleep(SLEEP);
                        }
                    }
//                    System.out.println("Finish read file \"" + fileToRead + "\"");
                } catch (IOException e) {
                    try {
                        close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        readThread.start();

    }


    private void resender(Socket as, final String sourceLabel) {
        try {
            InputStream is = as.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            StringBuilder stackTace = new StringBuilder();
            while (true) {

                LoggingEvent e = (LoggingEvent) ois.readObject();
                stackTace.setLength(0);

                stackTace.append(e.timeStamp).append(" ").append(e.getMessage());

                String[] strRep = e.getThrowableStrRep();
                if (strRep != null && strRep.length > 0) {
                    stackTace.setLength(0);
                    for (int i = 0; i < strRep.length; i++) {
                        if (i > 0) {
                            stackTace.append("\n");
                        }
                        stackTace.append(strRep[i]);
                    }
                    System.out.println(strRep);
                }
                readQueue.put(stackTace.toString());


//                ch.qos.logback.classic.Logger ll = (ch.qos.logback.classic.Logger) LSlogger;
//                ch.qos.logback.classic.spi.LoggingEvent event = new ch.qos.logback.classic.spi.LoggingEvent(e.fqnOfCategoryClass, ll,
//                        Level.toLevel(e.getLevel().toString()),
//                        e.getRenderedMessage() + "\n" + stackTace.toString(),
//                        e.getThrowableInformation() == null ? null : e.getThrowableInformation().getThrowable()
//                        , null);
//                event.setThreadName(e.getThreadName());
//                event.setTimeStamp(e.timeStamp);
//                event.setLoggerName(e.getLoggerName());

//                Field f;
//                try {
//                    f = e.getClass().getDeclaredField("mdcCopy");
//                    f.setAccessible(true);
//                    Hashtable iWantThis = (Hashtable) f.get(e); //IllegalAccessException
//                    if (iWantThis == null) {
//                        iWantThis = new Hashtable();
//                    }
//
//                    if (as.getRemoteSocketAddress() != null) {
//                        String hostString = as.getRemoteSocketAddress().toString();
//                        if(sourceLabel!=null && !sourceLabel.isEmpty()) {
//                            iWantThis.put("sourceLabel", sourceLabel);
//                        }
//                        iWantThis.put("fromHost", hostString.split("\\/")[0]);
//                        iWantThis.put("fromIP", hostString.split("[\\/:]")[1]);
//                        iWantThis.put("fromPort", hostString.split("[\\/:]")[2]);
//
//                    } else if (as.getInetAddress() != null && as.getInetAddress().getHostName() != null) {
//                        iWantThis.put("fromHost", as.getInetAddress().getHostName());
//                    }
//                    event.setMDCPropertyMap(iWantThis);
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
//
//                ll.callAppenders(event);

            }
        } catch (IOException e) {
            e.printStackTrace();
//            oLog.error("IOException", e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
//            oLog.error("ClassNotFoundException", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void checkClosed() {
        if (isClosed) {
            throw new RuntimeException("Reader is closed");
        }
    }

    public String readLine() throws IOException {
        try {
            while (paused.get() && !isClosed) {
                System.out.println("I'm asleep..");
                Thread.sleep(SLEEP);
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
        if (as != null && as.isConnected())
            try {
                as.close();
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
        return false;
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
