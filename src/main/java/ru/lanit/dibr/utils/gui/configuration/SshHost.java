package ru.lanit.dibr.utils.gui.configuration;

import com.jcraft.jsch.*;
import ru.lanit.dibr.utils.core.AbstractHost;
import ru.lanit.dibr.utils.utils.MyUserInfo;
import ru.lanit.dibr.utils.utils.ScpUtils;
import ru.lanit.dibr.utils.utils.SshUtil;
import ru.lanit.dibr.utils.utils.Utils;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class SshHost extends AbstractHost{

    private String pem;

    public SshHost(String host, int port, String user, String password) {
		this(null, host, port, user, password, null, null, null);
	}

	public SshHost(String description, String host, int port, String user, String password, String pem, String defaultEncoding, Tunnel tunnel) {
        super(description, host, port, user, password, defaultEncoding, tunnel);
		this.pem = pem;
	}

    public SshHost(String description, String host, int port, String user, String password, String pem, String defaultEncoding, String proxyAddress, int proxyPrort, String proxyType, Tunnel tunnel) {
        super(description, tunnel, host, port, user, password, defaultEncoding, proxyAddress, proxyPrort, proxyType, null, null);
        this.pem = pem;
    }

    public SshHost(String description, String host, int port, String user, String password, String pem, String defaultEncoding, String proxyAddress, int proxyPrort, String proxyType, String proxyLogin, String proxyPasswd, Tunnel tunnel) {
        super(description, tunnel, host, port, user, password, defaultEncoding, proxyAddress, proxyPrort, proxyType, proxyLogin, proxyPasswd);
        this.pem = pem;
    }

    public Session createSession(BlockingQueue<String> debugOutput, boolean useCompression) throws Exception {

        if(tunnel!=null) {
            Utils.writeToDebugQueue(debugOutput, "Open tunnels..");
            tunnel.connect(debugOutput, useCompression);
            useCompression = false;
        }

        if(!checkConnection(debugOutput)) { //ToDo: rewrite with void method which throw an exception
            String message = "ERROR: Can't connect to host!";
            message += "(" + debugOutput.peek() + ")";
            Utils.writeToDebugQueue(debugOutput, message);
            throw new Exception(message);
        }

        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        if (proxyHost != null) {
            Proxy proxy = null;
            if (proxyType.equals(HTTP)) {
                proxy = new ProxyHTTP(proxyHost, proxyPrort);
                if(proxyLogin != null) {
                    ((ProxyHTTP)proxy).setUserPasswd(proxyLogin, proxyPasswd);
                }
            } else if (proxyType.equals(SOCKS4)) {
                proxy = new ProxySOCKS4(proxyHost, proxyPrort);
                if(proxyLogin != null) {
                    ((ProxySOCKS4)proxy).setUserPasswd(proxyLogin, proxyPasswd);
                }
            } else if (proxyType.equals(SOCKS5)) {
                proxy = new ProxySOCKS5(proxyHost, proxyPrort);
                if(proxyLogin != null) {
                    ((ProxySOCKS5)proxy).setUserPasswd(proxyLogin, proxyPasswd);
                }
            } else {
                throw new Exception("Unknown proxy type! Please use one of following: '" + HTTP + "'; '" + SOCKS4 + "'; " + SOCKS5 + "'; ");
            }
            session.setProxy(proxy);
        }
        session.setConfig("StrictHostKeyChecking", "no"); //принимать неизвестные ключи от серверов
        //сжатие потока
        if(useCompression) {
        session.setConfig("compression.s2c", "zlib@openssh.com,zlib,none");
        session.setConfig("compression.c2s", "zlib@openssh.com,zlib,none");
        session.setConfig("compression_level", "9");
        }

        if (pem != null) {
            jsch.addIdentity(pem);
        } else {
            UserInfo ui = new MyUserInfo(password);
            session.setUserInfo(ui);
        }
        return session;
    }

    public Session connect(BlockingQueue<String> debugOutput) throws Exception {
        Utils.writeToDebugQueue(debugOutput, "Create session for host " + description + "..");
        Session session = createSession(debugOutput, true);
        Utils.writeToDebugQueue(debugOutput, "Connect session for host " + description + ".." );
        session.connect(30000);   // making a connection with timeout.
        Utils.writeToDebugQueue(debugOutput, "Session for host " + description + " are connected." );
        return session;
    }

    @Override
	public String toString() {
		return "host = " + host +
		        "; user = " + user + "; password = " + (password!=null?password.replaceAll(".", "*"):"") + "; pem = " + pem +
                "; proxyHost=" + proxyHost + "; proxyPort=" + proxyPrort + "; proxyType=" + proxyType + "; tunnel = [" + tunnel + "]";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

    @Override
    public String saveFullFile(LogFile logFile) {
        String savedFileName = null;
        try {
            // `basename /smartpaas-app/was/node1/WAShome/profiles/AppSrv01/logs/server1/stopServer.log`.` date +%Y%m%d%H%M%S -r /smartpaas-app/was/node1/WAShome/profiles/AppSrv01/logs/server1/stopServer.log`.zip
            String tmpDir = SshUtil.exec(this, "if [ -z \"$TMPDIR\" ]; then echo \"/tmp\"; else echo $TMPDIR; fi;", null).getData().trim();
            if (SshUtil.exec(this, "zip --help", null).getStatusCode() == 0) {
                //Use ZIP
                String zipFileName = SshUtil.exec(this, "echo " + tmpDir + "/`basename " + logFile.getPath() + "`.`date +%Y%m%d%H%M%S -r " + logFile.getPath() + "`.zip ", null).getData().trim();
                System.out.println("zipFileName: " + zipFileName);
                SshUtil.ExecResult execResult = SshUtil.exec(this, "zip -j -9 " + zipFileName + " " + logFile.getPath(), null);
                if (!(execResult.getData().contains("error") || execResult.getStatusCode() != 0)) {
                    savedFileName = ScpUtils.getFile(this, zipFileName, getDescription() + "_" + logFile.getName(), null);
                    SshUtil.exec(this, "rm " + zipFileName, null);
                    JOptionPane.showMessageDialog(null, "Файл \n'" + logFile.getPath() + "'\nс хоста\n'" + getHost() + "'\nзаархивирован и сохранён в катлоге программы как\n'" + savedFileName + "'.");
                } else {
                    savedFileName = saveFullFilePlain(logFile);
                }

            } else if (SshUtil.exec(this, "gzip --help", null).getStatusCode() == 0) {
                //gzip
                String gzipFileName = SshUtil.exec(this, "echo " + tmpDir + "/`basename " + logFile.getPath() + "`.`date +%Y%m%d%H%M%S -r " + logFile.getPath() + "`.gz ", null).getData().trim();
                System.out.println("gzipFileName: " + gzipFileName);
                SshUtil.ExecResult execResult = SshUtil.exec(this, "gzip -c9 " + logFile.getPath() + " > " + gzipFileName, null);
                if (!(execResult.getData().contains("error") || execResult.getStatusCode() != 0)) {
                    savedFileName = ScpUtils.getFile(this, gzipFileName, this.getDescription() + "_" + logFile.getName(), null);
                    SshUtil.exec(this, "rm " + gzipFileName, null);
                    JOptionPane.showMessageDialog(null, "Файл \n'" + logFile.getPath() + "'\nс хоста\n'" + this.getHost() + "'\nзаархивирован и сохранён в катлоге программы как\n'" + savedFileName + "'.");
                } else {
                    savedFileName = saveFullFilePlain(logFile);
                }
            } else {
                //Plain
                savedFileName = saveFullFilePlain(logFile);
            }
        } catch (JSchException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return savedFileName;
    }

    public String saveFullFilePlain(LogFile logFile) {
        String savedFileName;
        try {
            savedFileName = ScpUtils.getFile(this, logFile.getPath(), getDescription() + "_" + logFile.getName(), null);
            JOptionPane.showMessageDialog(null, "Файл \n'" + logFile.getPath() + "'\nс хоста\n'" + getHost() + "'\nсохранён в катлоге программы как\n'" + savedFileName + "'.");
            return savedFileName;
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
