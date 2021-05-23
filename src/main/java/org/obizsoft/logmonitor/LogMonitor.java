package org.obizsoft.logmonitor;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import org.obizsoft.logmonitor.config.Connection;
import org.obizsoft.logmonitor.config.File;

public class LogMonitor {

    public static void main(String[] args) {

        Config conf = ConfigFactory.parseFile(new java.io.File("conf.hocon")).resolve();
//        Config conf = ConfigValueFactory.fromAnyRef("/conf.hocon").atKey("/");
        System.out.println("conf = " + conf.root().render());

        Connection sshTest = ConfigBeanFactory.create(conf.getConfig("connections.ssh3"), Connection.class);
        System.out.println("sshTest.getDescription() = " + sshTest.getDescription());
        System.out.println("sshTest.getAuth().getLogin() = " + sshTest.getAuth().getLogin());
        System.out.println("sshTest.getOver().getAuth().getPassword() = " + sshTest.getOver().getAuth().getPassword());

        File file = ConfigBeanFactory.create(conf.getConfig("log-groups.dev2.files.gc-log"), File.class);
        System.out.println("file.getName() = " + file.getName());
        System.out.println("file.getConnection().getHost() = " + file.getConnection().getHost());

        org.obizsoft.logmonitor.config.Config config = ConfigBeanFactory.create(
                conf, org.obizsoft.logmonitor.config.Config.class);
        System.out.println("config.get(\"ssh1\").getAuth().getLogin() = " + config.getConnection("ssh1").getAuth().getLogin());
        System.out.println("config.getLogGroups().get(\"dev1\") = " + config.getLogGroups().get("dev1"));

        System.out.println("config.getLogGroup(\"dev2\").getFile(\"gc-log\").getName() = " + config.getLogGroup("dev2").getFile("gc-log").getName());
        System.out.println("config.getLogGroup(\"dev2\").getLogGroup(\"first\").getFile(\"log\").getName() = " + config.getLogGroup("dev2").getLogGroup("first").getFile("log").getName());
        System.out.println("config.getLogGroup(\"dev2\").getLogGroup(\"first\").getFile(\"log2\").getName() = " + config.getLogGroup("dev2").getLogGroup("first").getFile("log2").getName());
        System.out.println("config.getLogGroup(\"dev1\").getFile(\"first-file\").getFormat().getBlockPattern() = " + config.getLogGroup("dev1").getFile("first-file").getFormat().getBlockPattern());
    }
}
