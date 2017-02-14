package xml;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import java.util.Calendar;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import java.io.FileReader;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;

public class XmlRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		long beginTime = Calendar.getInstance().getTimeInMillis();
		String filename = "C:\\projects\\eclipse-ws\\default\\HTTP Test Runner\\data\\test-script\\login-logout.xml";
	
		//saxParserM1(filename);
		//new XmlRunner().xmlReaderParser(filename);
		domInit(filename);
		long endTime = Calendar.getInstance().getTimeInMillis();
		System.out.println("parsing took " + ((endTime - beginTime)) + " in millisec");		
	}

	public static void saxParserM1(String filename) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		TCustomHandler handler = new TCustomHandler();
		parser.parse(filename, handler);
	}
	
	public void xmlReaderParser(String filename) throws Exception {
		XMLReader reader = XMLReaderFactory.createXMLReader();
		TCustomHandler handler = new TCustomHandler();
		reader.setContentHandler(handler);
		reader.setErrorHandler(handler);
		FileReader fr = new FileReader(filename);
		reader.parse(new InputSource(fr));
	}
	
	public static void domInit(String name) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(name);
		
		
		
		NodeList list = document.getElementsByTagName("entry");
		//System.out.println("number of 'entry' element : " + list.getLength());
		
		//try to check which features are supported by that DOM implementation
		//System.out.println("supported ? : " + document.isSupported("Traversal", "2.0"));
		
		
		//all 'entry' elements
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			
			//System.out.println("node name : " + node.getNodeName() + " node value : " + node.getNodeValue() +
				//	" node type : " + node.getNodeType());
			NamedNodeMap attrMap = node.getAttributes();
			//System.out.println(attrMap.getNamedItem("method").getNodeValue()); //get
			//System.out.println(attrMap.getNamedItem("url").getNodeValue()); //get
			
			NodeList child_of_entry_with_headers_name = ((Element)node).getElementsByTagName("headers");
			Node headers_node = child_of_entry_with_headers_name.item(0);
			NodeList child_of_headers = ((Element)headers_node).getElementsByTagName("requestheaders");
			Node request_header = child_of_headers.item(0);
			NodeList headers = ((Element)request_header).getElementsByTagName("header");
			
			
			//for (int j = 0; j < headers.getLength(); j++)
				//System.out.println(j + "  -th  " + headers.item(j).getTextContent()); //get all values : except host, cookie and first header
			
			//headers.item(0).removeChild(request_header); //trying to remove node
						
			if (attrMap.getNamedItem("method").getNodeValue().equals("POST")) {
				//entry - PostData - params
				NodeList postDataList = ((Element)node).getElementsByTagName("PostData");
				Node postDataNode = postDataList.item(0);
				NodeList paramsList = ((Element)postDataNode).getElementsByTagName("params");
				Node paramsNode = paramsList.item(0);
				NodeList paramList = ((Element)paramsNode).getElementsByTagName("param");
				
				for (int k = 0; k < paramList.getLength(); k++) { //get all values
					NamedNodeMap nameAttrMap = paramList.item(k).getAttributes();
					System.out.println("param name : " + 
							nameAttrMap.getNamedItem("name").getNodeValue() +		 
							"   param value : " + 
							paramList.item(k).getTextContent());
				}
			}
		}
		
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		transformer.transform(new DOMSource(document), new StreamResult(new FileOutputStream(".\\data\\ura.xml")));
	}
}
