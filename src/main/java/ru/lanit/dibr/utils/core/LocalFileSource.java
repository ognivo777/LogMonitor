package ru.lanit.dibr.utils.core;

import ru.lanit.dibr.utils.gui.configuration.LocalSystem;
import ru.lanit.dibr.utils.gui.configuration.LogFile;
import ru.lanit.dibr.utils.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class LocalFileSource extends AbstractCachedLogSource {

    private LocalSystem host;
    private Runnable readThread;
    private long LOCAL_FILE_UPDATE_INTERVAL = 500;
    private File file;
    private long filePointer;

    public LocalFileSource(LocalSystem host, LogFile logFile) {
        super(logFile);
        this.host = host;
        file = new File(logFile.getPath());
        if(!(file.exists()&& file.canRead())) {
            Utils.writeToDebugQueue(debugOutput,"Can't open file " + logFile.getPath());
        }
        filePointer = file.length();
        //Если файл дюже велик ставим указаталь чтения на 10kb от конца. Иначе - в начало файла.
        if (filePointer > 20000) {
            filePointer -= 10000;
        } else {
            filePointer = 0;
        }
    }

    @Override
    public void startRead() throws Exception {
        isClosed = false;
        Utils.writeToDebugQueue(debugOutput, "Starting tailing '" + logFile.getName() + "' for host '" + host.getDescription() + "'..");
        readThread = new Runnable() {
            @Override
            public void run() {
                try {
                    while (!isClosed) {
                        Thread.sleep(LOCAL_FILE_UPDATE_INTERVAL);
                        long len = file.length();
                        if (len < filePointer) {
                            Utils.writeToDebugQueue(debugOutput,"Log file was reset. Restarting logging from start of file.");
                            filePointer = 0;
                        }
                        else if (len > filePointer) {
                            // File must have had something added to it!
                            //TODO тут проблемка с кодировкой.. Если utf-8 то нельзя точно сказать что текущее смещение от конца выровнено по символам
                            // Надо бы анализировать \n\r и по ним выравниваться,  читать байты между ними, а их уже в строки руками..
                            RandomAccessFile raf = new RandomAccessFile(file, "r");
                            try {
                                raf.seek(filePointer);
                                String line = null;
                                while ((line = readLineFromRAF(raf, logFile.getEncoding())) != null) {
                                    buffer.add(line);
                                }
                                filePointer = raf.getFilePointer();
                            } finally {
                                raf.close();
                            }
                        }
                    }
                } catch (Exception e) {
                    Utils.writeToDebugQueue(debugOutput, "Fatal error reading log file, log tailing has stopped.");
                }
            }
        };
        new Thread(readThread).start();

    }

    private String readLineFromRAF(RandomAccessFile raf, String encoding) throws IOException {
        StringBuffer input = new StringBuffer();
        int c = -1;
        boolean eol = false;
        ByteArrayOutputStream buff = new ByteArrayOutputStream();

        while (!eol) {
            switch (c = raf.read()) {
                case -1:
                case '\n':
                    eol = true;
                    break;
                case '\r':
                    eol = true;
                    long cur = raf.getFilePointer();
                    if ((raf.read()) != '\n') {
                        raf.seek(cur);
                    }
                    break;
                default:
                    buff.write(c);
                    break;
            }
        }
        input.append(new String(buff.toByteArray(), encoding));
        if ((c == -1) && (input.length() == 0)) {
            return null;
        }
        return input.toString();
    }


    @Override
    public void reloadFull() throws Exception {
        //todo
    }

    @Override
    public void close() throws Exception {
        isClosed = true;
    }

    @Override
    public String getName() {
        return host.getHost()+logFile.getPath()+logFile.getName();
    }

    @Override
    public boolean isWriteLineNumbers() {
        return false;
    }

    @Override
    public void setWriteLineNumbers(boolean writeLineNumbers) {

    }
}
