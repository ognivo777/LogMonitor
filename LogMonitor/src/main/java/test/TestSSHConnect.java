package test;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import ru.lanit.dibr.utils.gui.configuration.SshHost;
import ru.lanit.dibr.utils.utils.JschLogger;

/**
 * Created with IntelliJ IDEA.
 * User: Vladimir
 * Date: 26.07.16
 * Time: 17:49
 * To change this template use File | Settings | File Templates.
 */
public class TestSSHConnect {

    public static void main(String[] args) throws Exception {
        JSch jsch = new JSch();
        JSch.setLogger(new JschLogger());

        SshHost host = new SshHost("127.0.0.1", 4122, "tc", "Reverse1");
        Session s =  host.createSession(null, false);
        s.connect();
        System.out.println("Is Connected?: " + s.isConnected());
        s.disconnect();
        System.out.println("exit");

    }

}
