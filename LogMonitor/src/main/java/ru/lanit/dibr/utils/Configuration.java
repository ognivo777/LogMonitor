package ru.lanit.dibr.utils;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;
import java.io.IOException;
import java.io.File;

import ru.lanit.dibr.utils.core.AbstractHost;
import ru.lanit.dibr.utils.gui.configuration.*;

/**
 * User: VTaran
 * Date: 16.08.2010
 * Time: 22:07:28
 */
public class Configuration {

    private Map<AbstractHost, LinkedHashMap<String, LogFile>> servers;
    private Map<String, Tunnel> tunnels = new HashMap<String, Tunnel>();

    public Map<AbstractHost, LinkedHashMap<String, LogFile>> getServers() {
        return servers;
    }

    public void setServers(Map<AbstractHost, LinkedHashMap<String, LogFile>> servers) {
        this.servers = servers;
    }

    public Configuration(String path) {
        try {

            servers = new LinkedHashMap<AbstractHost, LinkedHashMap<String, LogFile>>();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(path));

            NodeList tunnelsList = doc.getElementsByTagName("tunnel");
            for (int i = 0; i < tunnelsList.getLength(); i++) {
                Node item = tunnelsList.item(i);
                AbstractHost nextHost = readHost(item);
                String name = item.getAttributes().getNamedItem("name").getNodeValue();

                List<Portmap> portmapList = new ArrayList<Portmap>();

                NodeList portmapNodeList = item.getChildNodes();
                for (int j = 0; j < portmapNodeList.getLength(); j++) {
                    Node portmapNode = portmapNodeList.item(j);
                    if (!portmapNode.getNodeName().equals("L")) {
                        continue;
                    }
                    int localPort = Integer.parseInt(portmapNode.getAttributes().getNamedItem("localPort").getNodeValue());
                    String destHost = portmapNode.getAttributes().getNamedItem("destHost").getNodeValue();
                    int destPort = Integer.parseInt(portmapNode.getAttributes().getNamedItem("destPort").getNodeValue());
                    portmapList.add(new Portmap(localPort, destHost, destPort));
                }

                tunnels.put(name, new Tunnel((SshHost) nextHost, portmapList));
            }


            NodeList serversList = doc.getElementsByTagName("server");
            for (int i = 0; i < serversList.getLength(); i++) {
                Node server = serversList.item(i);
                AbstractHost nextHost = readHost(server);

                System.out.println(nextHost);
                NodeList logList = server.getChildNodes();
                servers.put(nextHost, new LinkedHashMap<String, LogFile>());
                for (int j = 0; j < logList.getLength(); j++) {
                    if (logList.item(j).getNodeName().equals("log")) {
                        NamedNodeMap logElement = logList.item(j).getAttributes();
                        String name = logElement.getNamedItem("name").getNodeValue();
                        String file = logElement.getNamedItem("file").getNodeValue();
                        String blockPattern = null;
                        if (logElement.getNamedItem("blockPattern") != null) {
                            blockPattern = logElement.getNamedItem("blockPattern").getNodeValue().trim();
                            if (blockPattern.length() == 0) {
                                blockPattern = null;
                            }
                        }
                        servers.get(nextHost).put(name, new LogFile(name, file, blockPattern));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private AbstractHost readHost(Node server) {
        String descr = server.getAttributes().getNamedItem("name").getNodeValue();
        String host = server.getAttributes().getNamedItem("host").getNodeValue();
        String port = null;
        if (server.getAttributes().getNamedItem("port") != null) {
            port = server.getAttributes().getNamedItem("port").getNodeValue();
        }
        String user = server.getAttributes().getNamedItem("user").getNodeValue();
        String password = null;
        if (server.getAttributes().getNamedItem("password") != null) {
            password = server.getAttributes().getNamedItem("password").getNodeValue();
        }

        String encoding = null;
        if (server.getAttributes().getNamedItem("encoding") != null) {
            encoding = server.getAttributes().getNamedItem("encoding").getNodeValue();
        }
        if (encoding == null || encoding.trim().length() == 0) {
            encoding = System.getProperty("file.encoding");
        }

        String proxyHost = null;
        String proxyLogin = null;
        String proxyPasswd = null;
        String proxyType = null;
        String proxyPort = null;
        if (server.getAttributes().getNamedItem("proxyHost") != null) {
            proxyHost = server.getAttributes().getNamedItem("proxyHost").getNodeValue();
            if (server.getAttributes().getNamedItem("proxyType") != null) {
                proxyType = server.getAttributes().getNamedItem("proxyType").getNodeValue();
            }
            if (server.getAttributes().getNamedItem("proxyPort") != null) {
                proxyPort = server.getAttributes().getNamedItem("proxyPort").getNodeValue();
            }
            if (server.getAttributes().getNamedItem("proxyLogin") != null) {
                proxyLogin = server.getAttributes().getNamedItem("proxyLogin").getNodeValue();
            }
            if (server.getAttributes().getNamedItem("proxyPasswd") != null) {
                proxyPasswd = server.getAttributes().getNamedItem("proxyPasswd").getNodeValue();
            }
        }

        Tunnel tunnel = null;
        if (server.getAttributes().getNamedItem("tunnel") != null) {
            tunnel = tunnels.get(server.getAttributes().getNamedItem("tunnel").getNodeValue());
        }


        String serverType = null;
        if (server.getAttributes().getNamedItem("serverType") != null) {
            serverType = server.getAttributes().getNamedItem("serverType").getNodeValue();
        }

        AbstractHost nextHost = null;

        if (serverType == null || serverType.equalsIgnoreCase("SSH")) {
            if(port ==  null || port.isEmpty()) {
                port = "22";
            }
            //SSH specific
            String pem = null;
            if (server.getAttributes().getNamedItem("pem") != null) {
                pem = server.getAttributes().getNamedItem("pem").getNodeValue();
            }

            if (proxyHost != null) {
                if (proxyPort == null || proxyPort.trim().length() == 0) {
                    proxyPort = "0";
                }
                if (proxyLogin == null) {
                    nextHost = new SshHost(descr, host, Integer.parseInt(port), user, password, pem, encoding, proxyHost, Integer.parseInt(proxyPort), proxyType, tunnel);
                } else {
                    nextHost = new SshHost(descr, host, Integer.parseInt(port), user, password, pem, encoding, proxyHost, Integer.parseInt(proxyPort), proxyType, proxyLogin, proxyPasswd, tunnel);
                }

            } else {
                nextHost = new SshHost(descr, host, Integer.parseInt(port), user, password, pem, encoding, tunnel);
            }
        } else if (serverType.equalsIgnoreCase("FTP")) {
            if(port ==  null || port.isEmpty()) {
                port = "21";
            }

            if (proxyHost != null) {
                if (proxyPort == null || proxyPort.trim().length() == 0) {
                    proxyPort = "0";
                }
                if (proxyLogin == null) {
                    nextHost = new FTPHost(descr, tunnel, host, Integer.parseInt(port), user, password, encoding, proxyHost, Integer.parseInt(proxyPort), proxyType, null, null);
                } else {
                    nextHost = new FTPHost(descr, tunnel, host, Integer.parseInt(port), user, password, encoding, proxyHost, Integer.parseInt(proxyPort), proxyType, proxyLogin, proxyPasswd);
                }

            } else {
                nextHost = new FTPHost(descr, host, Integer.parseInt(port), user, password, encoding, tunnel);
            }

        } else if (serverType.equalsIgnoreCase("SMB")) {
            if(port ==  null || port.isEmpty()) {
                port = "445";
            }

            if (proxyHost != null) {
                if (proxyPort == null || proxyPort.trim().length() == 0) {
                    proxyPort = "0";
                }
                if (proxyLogin == null) {
                    nextHost = new CIFSHost(descr, tunnel, host, Integer.parseInt(port), user, password, encoding, proxyHost, Integer.parseInt(proxyPort), proxyType, null, null);
                } else {
                    nextHost = new CIFSHost(descr, tunnel, host, Integer.parseInt(port), user, password, encoding, proxyHost, Integer.parseInt(proxyPort), proxyType, proxyLogin, proxyPasswd);
                }

            } else {
                nextHost = new CIFSHost(descr, host, Integer.parseInt(port), user, password, encoding, tunnel);
            }

        }
        return nextHost;
    }
}
