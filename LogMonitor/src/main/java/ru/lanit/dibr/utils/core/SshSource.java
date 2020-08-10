package ru.lanit.dibr.utils.core;

import com.jcraft.jsch.*;
import ru.lanit.dibr.utils.gui.configuration.SshHost;
import ru.lanit.dibr.utils.gui.configuration.LogFile;
import ru.lanit.dibr.utils.utils.SshUtil;
import ru.lanit.dibr.utils.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * User: Vova
 * Date: 13.11.12
 * Time: 2:12
 */
public class SshSource extends AbstractCachedLogSource {

    private Thread readThread;
    private SshHost host;

    BufferedReader reader = null;
    ChannelExec channel = null;
    Session session = null;

    public SshSource(SshHost host, LogFile logFile) {
        super(logFile);
        this.host = host;
    }

    public void startRead() throws Exception {
        session = host.connect(debugOutput);
        isClosed = false;
        channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("tail -n 3000 -f " + logFile.getPath());
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
                        synchronized (buffer) {
                            buffer.add(0, nextLine);
                            if (buffer.size() > MAX_CACHED_LINES && buffer.getLast() != lastLine) {
                                buffer.removeLast();
                            }
                        }
                    }
                } catch (IOException e) {
                    //todo перехватить и в дебаг отправить, использовать логгеры
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
        readThread.start();
    }

    /** загружает в буффер целиком весь файл с сервера. На данный момент не используется */
    public void reloadFull() throws Exception {
        if (channel != null) {
            channel.sendSignal("KILL");
            channel.setCommand("cat " + logFile.getPath());
        }
    }

    public void close() {
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

    public boolean isWriteLineNumbers() {
        return false;
    }

    public void setWriteLineNumbers(boolean writeLineNumbers) {
//        this.writeLineNumbers = writeLineNumbers;
    }

    @Override
    public String getName() {
        return host.getHost()+logFile.getPath()+logFile.getName();
    }
}
