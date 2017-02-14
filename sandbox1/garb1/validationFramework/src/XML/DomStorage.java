
/*
 * Created on Jul 26, 2005
 */

package xml;

import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;
import org.w3c.dom.Document;

import config.Config;

/*
 This class is used to have an access to DOM-Document presentation of 
 test plans
 */

public class DomStorage {
	private Hashtable documents;
	
	public DomStorage(String[] names) {
		documents = new Hashtable();
		process(names);
	}
	
	private void process(String[] names) {
		DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
		docBuildFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuildFactory.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		String tpPath = Config.getInstance().getTestPlansPath(); 
		for (int i = 0, max1 = names.length; i < max1; i++) {
			File file = new File(tpPath + "\\"+ names[i]);
			try {
				documents.put( names[i], docBuilder.parse(file));
			} catch (SAXException se) {
				se.printStackTrace();
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}
	}
	
	public Document getByName(String name) {
		return (Document) documents.get(name);
	}
}
