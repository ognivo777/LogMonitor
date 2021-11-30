package ru.lanit.dibr.utils.core;

import it.sauronsoftware.ftp4j.*;
import ru.lanit.dibr.utils.gui.configuration.FTPHost;
import ru.lanit.dibr.utils.gui.configuration.LogFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Vova on 19.02.2015.
 */
public class FtpSource implements LogSource {
    private boolean isClosed = false; //TODO: atomic
    private boolean paused = false;
    private boolean writeLineNumbers = false;
    private Thread ftpReadThread;


    List<String> buffer;

    int readedLines;
    private ru.lanit.dibr.utils.core.AbstractHost host;
    private LogFile logFile;
    BufferedReader reader = null;
    FTPClient client;
    long ftpFileSize;

    public FtpSource(FTPHost host, LogFile logFile) {
        this.host = host;
        this.logFile = logFile;
    }

    public void startRead() throws Exception {
        //checkClosed();
        try {
            readedLines = 0;
            paused = false;
            buffer = new ArrayList<String>();

            client = new FTPClient();
            client.setCompressionEnabled(true);

            client.setType(FTPClient.TYPE_BINARY);
            client.connect(host.getHost(), host.getPort());
            client.login(host.getUser(), host.getPassword());
            ftpFileSize = client.fileSize(logFile.getPath());
            ftpFileSize = ftpFileSize > 50000 ? ftpFileSize - 50000 : 0;


            isClosed = false;
            final PipedOutputStream pos = new PipedOutputStream();
            reader = new BufferedReader(new InputStreamReader(new PipedInputStream(pos), host.defaultEncoding));


            //FTP read thread
            ftpReadThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Запуск треда чтения из FTP");
                    java.util.Date md = null;
                    try {
                        String nextLine;
                        while (!isClosed) {
                            System.out.println("Ftp read thread still work..");
                            if (!isPaused()) {
                                String dir = client.currentDirectory();
                                client.changeDirectoryUp();
                                client.changeDirectory(dir);

                                md = client.modifiedDate(logFile.getPath());
                                System.out.println("Modification date :" + md + "; and size: " + ftpFileSize);
                                if (client.fileSize(logFile.getPath()) != ftpFileSize) {
                                    client.download(logFile.getPath(), pos, ftpFileSize, null);
                                    ftpFileSize = client.fileSize(logFile.getPath());

                                }
                            } else {
                                System.out.println("FTP is paused..");
                            }
                            Thread.sleep(300);
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
                    while (client.isConnected() && !isClosed) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!isClosed) {
                        System.out.println("Connection failed!");
                        ftpReadThread.interrupt();
                        try {
                            close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Stopped FTP connection monitor thread.");
                    }

                }
            }, "FTP connection monitor").start();

            System.out.println("1");
            Thread.sleep(500);
            System.out.println("2");
            System.out.println("Starting monitoring FTP modifications and read new lines..");
            ftpReadThread.start();
            System.out.println("FTP monitoring STARTED.");
        } catch (Exception e) {
            System.out.println("FTP start read error:");
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
        return LogSource.SingletonSkipLineValue.SKIP_LINE;
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
            System.out.println("FTP source already closed!");
            return;
        }
        System.out.println("Closing FTP log source..");
        isClosed = true;
        if(ftpReadThread!=null)
            ftpReadThread.interrupt();
        if (reader != null)
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (client != null && client.isConnected()) {
            System.out.println("Try to disconnect FTP..");
            client.disconnect(true);
            System.out.println("FTP  disconnected.");
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
