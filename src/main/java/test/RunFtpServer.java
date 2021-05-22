package test;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.BaseUser;

import java.io.File;
import java.io.IOException;

/**
 * Created by Vova on 15.02.2015.
 */
public class RunFtpServer {
    public static void main(String[] args) throws FtpException, IOException, InterruptedException {

//        createTestUser();
        runServer();
        LogGenerator.generateTo("ftp/tst.log");
    }

    public static void createTestUser() throws FtpException {
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        userManagerFactory.setFile(new File("ftpusers.properties"));
        userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());
        UserManager um = userManagerFactory.createUserManager();

        BaseUser user = new BaseUser();
        user.setName("user");
        user.setPassword("password");
        user.setHomeDirectory("ftp");

        um.save(user);
    }

    public static void runServer() throws FtpException {
        FtpServerFactory serverFactory = new FtpServerFactory();

        ListenerFactory factory = new ListenerFactory();

// set the port of the listener
        factory.setPort(2221);

// replace the default listener
        serverFactory.addListener("default", factory.createListener());

        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
//        userManagerFactory.setFile(new File("ftpusers.properties"));
        userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());
        UserManager um = userManagerFactory.createUserManager();

        BaseUser user = new BaseUser();
        user.setName("user");
        user.setPassword("password");
        user.setHomeDirectory("ftp");

        um.save(user);

        serverFactory.setUserManager(um);

// start the server
        FtpServer server = serverFactory.createServer();

        server.start();
    }
}
