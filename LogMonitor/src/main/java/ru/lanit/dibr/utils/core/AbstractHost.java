package ru.lanit.dibr.utils.core;

import com.jcraft.jsch.JSchException;
import ru.lanit.dibr.utils.gui.configuration.LogFile;
import ru.lanit.dibr.utils.gui.configuration.Tunnel;
import ru.lanit.dibr.utils.utils.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Vova on 19.02.2015.
 */
public abstract class AbstractHost {
    protected String description;
    protected Tunnel tunnel;
    protected String host;
    protected int port;
    protected String user;
    protected String password;
    protected String defaultEncoding;
    protected String proxyHost;
    protected int proxyPrort;
    protected String proxyType;
    protected String proxyLogin;
    protected String proxyPasswd;

    public static final String SOCKS5="SOCKS5";
    public static final String SOCKS4="SOCKS4";
    public static final String HTTP="HTTP";


    public AbstractHost(String description, String host, int port, String user, String password, String defaultEncoding, Tunnel tunnel) {
        this.description = description;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.defaultEncoding = defaultEncoding;
        this.tunnel = tunnel;
    }

    public AbstractHost(String description, Tunnel tunnel, String host, int port, String user, String password, String defaultEncoding, String proxyHost, int proxyPrort, String proxyType, String proxyLogin, String proxyPasswd) {
        this.description = description;
        this.tunnel = tunnel;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.defaultEncoding = defaultEncoding;
        this.proxyHost = proxyHost;
        this.proxyPrort = proxyPrort;
        this.proxyType = proxyType;
        this.proxyLogin = proxyLogin;
        this.proxyPasswd = proxyPasswd;
    }

    public String getDescription() {
        return description;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    public Tunnel getTunnel() {
        return tunnel;
    }

    public boolean checkCnnection() {
        return checkConnection(null);
    }


    public synchronized boolean checkConnection(BlockingQueue<String> debugOutput) {
        if(tunnel!=null && !tunnel.isConnectionAlive()) {
            return false;
        }
        //Utils.writeToDebugQueue(debugOutput, "Check connection to: '" + host + ":" + port + "'..");

        boolean isReachable = false;
        try {
            isReachable = InetAddress.getByName(host).isReachable(1000);
        } catch (IOException e) {
            isReachable = false;
        }

        if(!isReachable) {
            Utils.writeToDebugQueue(debugOutput, "Host seems unreachable!");
//            return false;
        }

        InetSocketAddress socketAddress = new InetSocketAddress(host, port);
        try {
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(true);
            channel.connect(socketAddress);
            boolean result = channel.isConnected();
            //Utils.writeToDebugQueue(debugOutput, "Check connection result: " + result);
            return channel.isConnected();
        } catch (UnresolvedAddressException e) {
            e.printStackTrace();
            Utils.writeToDebugQueue(debugOutput, "Check connection result: UnresolvedAddressException" + e.getMessage());
            return false;
        } catch (UnsupportedAddressTypeException e)
        {
            e.printStackTrace();
            Utils.writeToDebugQueue(debugOutput, "Check connection result: UnsupportedAddressTypeException" + e.getMessage());
            return false;
        } catch (SecurityException e)
        {
            e.printStackTrace();
            Utils.writeToDebugQueue(debugOutput, "Check connection result: SecurityException" + e.getMessage());
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
            Utils.writeToDebugQueue(debugOutput, "Check connection result: IOException" + e.getMessage());
            return false;
        }

    }

    public abstract String saveFullFile(LogFile logFile) ;
    public abstract String saveFullFilePlain(LogFile logFile) ;
}
