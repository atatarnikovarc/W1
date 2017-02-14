package lumber;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class JmxXmlHandler extends DefaultHandler {
	private int sElmCount = 0;
	public void startElement(String namespaceURI, String sName, // simple name
							 String qName, // qualified name
	                         Attributes attrs) throws SAXException {
		//System.out.println(qName);
		//System.out.println(sName);
		if ("testelement".equals(qName))
			if (attrs.getValue("class").equals("org.apache.jmeter.reporters.ResultCollector")) {
				
				System.out.println("Elm!");
			}
	}
}
