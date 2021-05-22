package ru.lanit.dibr.utils.utils;

import it.sauronsoftware.ftp4j.FTPClient;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vova on 15.02.2015.
 */
public class FtpTailer {
    private FTPClient client;
    private String fileName;
    private Runnable readThread;
    private boolean _running = true;
    private long _updateInterval = 50;
    private File _file;
    private long _filePointer;
    private BlockingQueue<String> buffer = new LinkedBlockingQueue<String>();
    private boolean isStarted = false;

    public FtpTailer(String fileName) {
        this.fileName = fileName;
        _file = new File(fileName);
        if(!(_file.exists()&&_file.canRead())) {
            //todo error
        }

    }

    public String readLine() throws InterruptedException {
        if(isStarted) {
            return buffer.poll(1, TimeUnit.MINUTES);
        } else {
            //todo error
            return null;
        }
    }

    public void start() {
        if (isStarted) {
            //todo error
        }
        isStarted = true;
        readThread = new Runnable() {
            @Override
            public void run() {
                try {
                    while (_running) {
                        Thread.sleep(_updateInterval);
                        long len = _file.length();
                        if (len < _filePointer) {
                            // Log must have been jibbled or deleted.
                            this.appendMessage("Log file was reset. Restarting logging from start of file.");
                            _filePointer = len;
                        }
                        else if (len > _filePointer) {
                            // File must have had something added to it!
                            RandomAccessFile raf = new RandomAccessFile(_file, "r");
                            raf.seek(_filePointer);
                            String line = null;
                            while ((line = raf.readLine()) != null) {
                                this.appendLine(line);
                            }
                            _filePointer = raf.getFilePointer();
                            raf.close();
                        }
                    }
                }
                catch (Exception e) {
                    this.appendMessage("Fatal error reading log file, log tailing has stopped.");
                }
                // dispose();
            }

            private void appendLine(String line) {
                buffer.add(line);
            }

            private void appendMessage(String s) {
                System.out.println(s);
            }
        };
        new Thread(readThread).start();
    }
}
