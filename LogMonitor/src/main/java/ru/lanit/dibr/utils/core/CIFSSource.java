package ru.lanit.dibr.utils.core;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import ru.lanit.dibr.utils.gui.configuration.CIFSHost;
import ru.lanit.dibr.utils.gui.configuration.LogFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Vova on 19.02.2015.
 */
public class CIFSSource implements LogSource {
    private boolean isClosed = false; //TODO: atomic
    private boolean paused = false;
    private boolean writeLineNumbers = false;
    private Thread cifsReadThread;


    List<String> buffer;

    int readedLines;
    private AbstractHost host;
    private LogFile logFile;
    BufferedReader reader = null;
    SmbFile smbFile;
    long fileSize = 0;

    public CIFSSource(CIFSHost host, LogFile logFile) {
        this.host = host;
        this.logFile = logFile;
    }

    public void startRead() throws Exception {
        //checkClosed();
        try {
            readedLines = 0;
            paused = false;
            buffer = new ArrayList<String>();

            String smbUrl = "smb://";
            if(host.getUser() != null && !host.getUser().isEmpty()) {
                smbUrl += host.getUser() + ":" + host.getPassword() + "@";

            }

            smbUrl += host.getHost() + ":" + host.getPort() + "/" + logFile.getPath();

            System.out.println("smbUrl = " + smbUrl);

            smbFile = new SmbFile(smbUrl);
            smbFile.connect();
            System.out.println("Exist: " + smbFile.exists());
            System.out.println("canRead: " + smbFile.canRead());
            System.out.println("FileSize: " + smbFile.getContentLength());
//            fileSize = smbFile.getContentLength();
//            fileSize = fileSize > 50000 ? fileSize - 50000 : 0;


            isClosed = false;
            final PipedOutputStream pos = new PipedOutputStream();
            reader = new BufferedReader(new InputStreamReader(new PipedInputStream(pos), host.defaultEncoding));

            //cifs read thread
            cifsReadThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Запуск треда чтения из cifs");
                    try {
                        while (!isClosed) {
                            System.out.println("cifs read thread still work..");
                            if (!isPaused()) {
                                System.out.println("" + System.currentTimeMillis() + " prev size: " + fileSize);
                                int contentLength = smbFile.getContentLength();
                                System.out.println("" + System.currentTimeMillis() + " curr size: " + contentLength);
                                if (contentLength > fileSize) {
                                    InputStream smbFileIunputStream = new SmbFileInputStream(smbFile);
                                    smbFileIunputStream.skip(fileSize);
                                    byte [] tmp = new byte[8192];
                                    long totalReaded = 0;
                                    int readed = 0;
                                    while((readed = smbFileIunputStream.read(tmp)) > 0) {
                                        pos.write(tmp, 0, readed);
                                        System.out.write(tmp, 0, readed);
                                        totalReaded += readed;
                                    }
                                    pos.flush();
                                    smbFileIunputStream.close();
                                    System.out.println("cifs readed = " + totalReaded);
                                    fileSize += totalReaded;
                                }
                            } else {
                                System.out.println("cifs is paused..");
                            }
                            Thread.sleep(500);
                        }

                    } catch (Exception e) {
                        try {
                            close();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String nextLine;
                    try {
                        while ((nextLine = reader.readLine()) != null && !isClosed) {
                            //                        buffer.add(String.format("%6d: %s", (buffer.size()+1), nextLine));
                            buffer.add(nextLine);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

            //Connection monitoring thread
            new Thread(new Runnable() {
                public void run() {
                    System.out.println("6");
                    while (!isClosed) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!isClosed) {
                        System.out.println("Connection failed!");
                        cifsReadThread.interrupt();
                        try {
                            close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Stopped cifs connection monitor thread.");
                    }

                }
            }, "cifs connection monitor").start();

            System.out.println("1");
            Thread.sleep(500);
            System.out.println("2");
            System.out.println("Starting monitoring cifs modifications and read new lines..");
            cifsReadThread.start();
            System.out.println("cifs monitoring STARTED.");
        } catch (Exception e) {
            System.out.println("cifs start read error:");
            e.printStackTrace();
            throw new Exception(e);
        }

    }

    public String readLine() throws IOException {
        try {
            while (paused && !isClosed) {
                System.out.println("I'm asleep..");
                Thread.sleep(200);
            }
            if(isClosed) {
                throw new IOException("Connection lost.");
            }
            if (buffer.size() > readedLines) {
                if(writeLineNumbers) {
                    return String.format("%6d: %s", readedLines + 1, buffer.get(readedLines++));
                } else {
                    return buffer.get(readedLines++);
                }
            } else {
                Thread.sleep(150);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return SingletonSkipLineValue.SKIP_LINE;
    }

    public void reset() {
        readedLines = 0;
        //reader.reset();
    }

    public void reloadFull() throws Exception {
        //TODO
    }

    public void close() throws Exception {
        if(isClosed) {
            System.out.println("cifs source already closed!");
            return;
        }
        System.out.println("Closing cifs log source..");
        isClosed = true;
        if(cifsReadThread!=null)
            cifsReadThread.interrupt();
        if (reader != null)
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (smbFile != null ) {
            System.out.println("Try to disconnect cifs..");
            System.out.println("Do nothing..");
            System.out.println("cifs  disconnected.");
        }
        buffer.clear();
    }

    public void setPaused(boolean paused) {
        System.out.println("set paused: " + paused);
        this.paused = paused;
    }

    public boolean isWriteLineNumbers() {
        return writeLineNumbers;
    }

    public void setWriteLineNumbers(boolean writeLineNumbers) {
        this.writeLineNumbers = writeLineNumbers;
    }

    @Override
    public BlockingQueue<String> getDebugOutput() {
        return debugOutput;
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public String getName() {
        return host.getHost()+logFile.getPath()+logFile.getName();
    }
}
