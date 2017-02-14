package xml;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

public class TCustomHandler extends DefaultHandler {
	
	public TCustomHandler() {
		super();
	}
	
	public void startDocument() {
		System.out.println("document's begin : ");
	}
	
	public void endDocument() {
		System.out.println("document's end : ");
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals("entry")) {
			System.out.println("entity element occured ");
			System.out.println(attributes.getValue("method"));
			System.out.println(attributes.getValue("url"));
		}
	}
}
