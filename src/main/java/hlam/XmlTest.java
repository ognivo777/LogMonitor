package hlam;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.beans.XMLDecoder;

import ru.lanit.dibr.utils.gui.configuration.SshHost;

/**
 * User: VTaran
 * Date: 16.08.2010
 * Time: 22:44:38
 */
public class XmlTest {

	public void testReadConf() {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        	DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File("settings.xml"));

			XMLDecoder dec = new XMLDecoder(new FileInputStream("settings.xml"));
			System.out.println(dec.readObject());

			NodeList list = doc.getElementsByTagName("server");
			for (int i = 0; i < list.getLength(); i++) {
				String host = list.item(i).getAttributes().getNamedItem("host").getNodeValue();
				String user = list.item(i).getAttributes().getNamedItem("user").getNodeValue();
				String password = list.item(i).getAttributes().getNamedItem("password").getNodeValue();
				SshHost nextHost = new SshHost(host, 22, user, password);
				System.out.println(nextHost);

//				for(int i; ) {
//				}
			}
			System.out.println(doc.getFirstChild().getNodeName());
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (SAXException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (ParserConfigurationException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		
	}
}
