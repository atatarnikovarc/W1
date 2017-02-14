package app.xml;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import app.business.dto.PassengerDTO;
import app.util.Config;

public class XmlHelper {

	private static XmlHelper instance = new XmlHelper();

	private Document document;

	private XmlHelper() {
		buildDocument();
	}

	private void buildDocument() {
		DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory
				.newInstance();
		docBuildFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = null;

		try {
			docBuilder = docBuildFactory.newDocumentBuilder();
			this.document = docBuilder.parse(new File(Config.getInstance()
					.getLuggageFile()));
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static XmlHelper getInstance() {
		return instance;
	}

	public List<PassengerDTO> getPassengersByName(String name) {
		NodeList passengerList = document.getElementsByTagName("passenger");
		List<PassengerDTO> dtoList = new Vector<PassengerDTO>();

		
			for (int i = 0; i < passengerList.getLength(); i++) {
				Node currentPassenger = passengerList.item(i);
				
				if (name.equals(currentPassenger.getAttributes().item(0)
						.getNodeValue())) {
					String passengerName = currentPassenger.getAttributes()
							.item(0).getNodeValue();
					String luggageId = currentPassenger.getChildNodes().item(1)
							.getAttributes().item(0).getNodeValue();
					String flightNumber = currentPassenger.getParentNode()
							.getAttributes().item(0).getNodeValue();

					dtoList.add(new PassengerDTO(passengerName, flightNumber,
							luggageId));
				}
			}
		

		return dtoList;
	}
}
