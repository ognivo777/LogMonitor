package ru.lanit.dibr.utils.gui.configuration;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import ru.lanit.dibr.utils.utils.Utils;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: Vova
 * Date: 22.10.13
 * Time: 0:39
 */
public class Tunnel {
    private SshHost host;
    private List<Portmap> portmaps;
    private AtomicBoolean isConnected = new AtomicBoolean(false);
    private Session session;

    public Tunnel(SshHost host, List<Portmap> portmaps) {
        this.host = host;
        this.portmaps = portmaps;
    }

    @Override
    public String toString() {
        String str = "";
        for (Portmap portmap : portmaps) {
            str += portmap.toString();
        }
        return str + "; " + super.toString();
    }

    public synchronized void connect(final BlockingQueue<String> debugOutput, boolean useCompression) {
        if(isConnected.get()) {
            if(isConnectionAlive()) {
                Utils.writeToDebugQueue(debugOutput, "Tunnel already connected.");
                return;
            } else {
                close();
            }
        } else if(session!=null && session.isConnected()) {
            close();
        }
        try {
            Utils.writeToDebugQueue(debugOutput, "Create tunnel [" + host.getDescription() + "]..");
            if(!checkConnection(null)) {
                throw new RuntimeException("Tunnel host " + host.getHost() + ":" + host.getPort() + " unreachable!");
            }
            session = host.createSession(debugOutput, useCompression);
            Utils.writeToDebugQueue(debugOutput, "Create tunnel [" + host.getDescription() + "] port mappings..");
            for (Portmap portmap : portmaps) {
                Utils.writeToDebugQueue(debugOutput, "L" + portmap.getLocalPort() + " -> " + portmap.getDestHost() + ":"+portmap.getDestPort());
                session.setPortForwardingL(portmap.getLocalPort(), portmap.getDestHost(), portmap.getDestPort());
            }
            Utils.writeToDebugQueue(debugOutput, "Try to open tunnel [" + host.getDescription() + "]");
            session.connect(30000);
            isConnected.set(true);
            Utils.writeToDebugQueue(debugOutput, "Tunnel [" + host.getDescription() + "] are connected. Starting keepalive message thread..");

            new Thread(new Runnable() {
                public void run() {
                    String message = "";
                    while (session.isConnected()) {
                        try {
                            session.sendKeepAliveMsg();
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            message = e.getMessage();
                            e.printStackTrace();
                        } catch (Exception e) {
                            message = e.getMessage();
                            e.printStackTrace();
                        }
                    }
                    Utils.writeToDebugQueue(debugOutput, "Tunnel session disconnected! error message: " + message);
                    close();
                }
            }, "tunel connection monitor").start();


        } catch (Exception e) {
            Utils.writeToDebugQueue(debugOutput, "Error on open tunnel to host: " + host.getDescription());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public boolean isConnectionAlive() {
        if(isConnected.get() && session.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkConnection() {
        return checkConnection(null);
    }

    public boolean checkConnection(BlockingQueue<String> debugOutput) {
//        Utils.writeToDebugQueue(debugOutput, "Check tunnel connection..");
        return host.checkConnection(debugOutput);
    }

    public void close() {
        try {
            if(session!=null && session.isConnected()) {
                for (Portmap portmap : portmaps) {
                    try {
                        session.delPortForwardingL(portmap.getLocalPort());
                    } catch (JSchException e) {
                        e.printStackTrace();
                    }
                }
                session.disconnect();
            }
        } finally {
            isConnected.set(false);
        }

    }

}
