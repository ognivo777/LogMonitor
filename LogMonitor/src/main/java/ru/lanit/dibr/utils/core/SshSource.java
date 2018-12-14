package ru.lanit.dibr.utils.core;

import com.jcraft.jsch.*;
import ru.lanit.dibr.utils.gui.configuration.SshHost;
import ru.lanit.dibr.utils.gui.configuration.LogFile;
import ru.lanit.dibr.utils.utils.SshUtil;
import ru.lanit.dibr.utils.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

/**
 * User: Vova
 * Date: 13.11.12
 * Time: 2:12
 */
public class SshSource implements LogSource {

    private boolean isClosed = false;
    private boolean paused = false;
//    private boolean writeLineNumbers = false;
    private Thread readThread;

    LinkedList<String> buffer;
    private Iterator<String> descIter;

//    int readedLines;
    String lastLine = null;

    private SshHost host;
    private LogFile logFile;
    BufferedReader reader = null;
    ChannelExec channel = null;
    Session session = null;

    public SshSource(SshHost host, LogFile logFile) {
        this.host = host;
        this.logFile = logFile;
    }

    public void startRead() throws Exception {
        //checkClosed();
//        readedLines = 0;
        paused = false;
        buffer = new LinkedList<String>();
        session = host.connect(debugOutput);
        isClosed = false;
        channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("tail -n 2000 -f " + logFile.getPath());
        //channel.setCommand("tail -c +0 -f " + logFile.getPath()); //Так можно загрузить весь файл
        reader = new BufferedReader(new InputStreamReader(channel.getInputStream(), host.getDefaultEncoding()));
        Utils.writeToDebugQueue(debugOutput, "Starting tailing '" + logFile.getName() + "' for host '" + host.getDescription() + "'..");
        channel.connect(30000);
        Utils.writeToDebugQueue(debugOutput, "Tailing '" + logFile.getName() + "' for host '" + host.getDescription() + "' are started. Channel id: " + channel.getId());

        //Запуск треда, который читает строки из SSH и кладёт в буффер.
        readThread = new Thread(new Runnable() {
            public void run() {
                String nextLine;
                try {
                    while ((nextLine = reader.readLine()) != null && !isClosed) {
                        buffer.add(0, nextLine);
                        if(buffer.size()> MAX_CACHED_LINES && buffer.getLast()!=lastLine ) {
                            buffer.removeLast();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
                System.out.println("Stopped SSH read thread.");
            }
        }, "SSH2BufferReader");

        // Запуск треда, который мониторит состояние соединения
//        new Thread(new Runnable() {
//            public void run() {
//                while(channel.isConnected() && !isClosed && (host.getTunnel()==null || host.getTunnel().isConnectionAlive())) {
//                    try {
//                        if(host.getTunnel()!=null) {
//                            if(!host.getTunnel().isConnectionAlive()) {
//                                System.out.println("Tunnel are disconnected!");
//                                close();
//                                break;
//                            }
//                        }
//                        Thread.sleep(1500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                if(!isClosed) {
//                    System.out.println("Connection failed!");
//                    readThread.interrupt();
//                    try {
//                        close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    System.out.println("Stopped SSH connection monitor thread.");
//                }
//
//            }
//        }, "Connection monitor").start();

        readThread.start();

    }

    /** возвращает очередную строку, либо константу "EMPTY_LINE". Если SSHSourсе поставлен на паузу - просто висит ожидая снятия паузы */
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

    /** сбрасывает счётчик прочитанных строк на 0. Следующий вызов readLine() вернёт первую строку из буффера. */
    public void reset() {
        lastLine = null;
        //reader.reset();
    }

    /** загружает в буффер целиком весь файл с сервера. На данный момент не используется */
    public void reloadFull() throws Exception {
        if (channel != null) {
            channel.sendSignal("KILL");
            channel.setCommand("cat " + logFile.getPath());
        }
    }

    public void close() throws Exception {
        if(isClosed) {
            System.out.println("SSH source already closed!");
            return;
        }
        System.out.println("Closing SSH log source..");
        isClosed = true;
        if(readThread!=null) {
            readThread.interrupt();
        }
        if (reader != null)
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        if (channel != null && channel.isConnected()) {
            System.out.println("try to disconnect SSH channel:");
            try {
                System.out.println("Sending KILL signal.");
            channel.sendSignal("KILL");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                System.out.println("Sending TERM signal.");
                channel.sendSignal("TERM");
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Sending exit command.");
            channel.setCommand("exit");
            channel.disconnect();
            System.out.println("SSH channel disconnected");
        }
        if (session != null && session.isConnected()) {
            SshUtil.exec(host, "ps -e -o pid,ppid,comm | grep \"1 tail\" | awk '{print $1}' | xargs kill -9", debugOutput);
            System.out.println("try to disconnect SSH session");
            session.disconnect();
            System.out.println("SSH session disconnected");
        }
        buffer.clear();
    }

    public void setPaused(boolean paused) {
        System.out.println("set paused: " + paused);
        this.paused = paused;
    }

    public boolean isWriteLineNumbers() {
        return false;
    }

    public void setWriteLineNumbers(boolean writeLineNumbers) {
//        this.writeLineNumbers = writeLineNumbers;
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public String getName() {
        return host.getHost()+logFile.getPath()+logFile.getName();
    }

    @Override
    public BlockingQueue<String> getDebugOutput() {
        return debugOutput;
    }

}
