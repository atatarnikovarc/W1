/*
 * Created on Jul 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package lumber;

import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;



/**
 * @author atatarnikov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MappingsReader {
	private static final String rootelementname = "requirementmapping";
	//private static final String 
	private List mappings;
	
	public MappingsReader(String mappingFilename) throws Exception {
		File mappingsFile = new File(mappingFilename);
		DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
		docBuildFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();
		Document currentDoc = docBuilder.parse(mappingsFile);
		NodeList currentList = currentDoc.getElementsByTagName(MappingsReader.rootelementname);
		mappings = new Vector();
		process(currentList);
	}
	
	private void process(NodeList list) {
		for (int i = 0, max1 = list.getLength(); i < max1; i++) {
			Node currentRootElm = list.item(i);
			NodeList childsOfCurrentRootElm = currentRootElm.getChildNodes();
			
		}
	}
	
	private String getNodeValueByName(String name, NodeList list) {
		String result = null;
		
		for (int i = 0, max1 = list.getLength(); i < max1; i++) {
			if (list.item(i).getNodeName().equals(name)) {
				result = new String(list.item(i).getNodeValue());
				break;
			}
		}
		
		return result;
	}
	
	public MappingDescriptor getMapping(int index) {
		return (MappingDescriptor)mappings.get(index);
	}
}
