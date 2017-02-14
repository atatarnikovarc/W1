/*
 * Created on Jun 2, 2005
*/

package core;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class LogUpdater {

	private static final String pathToJmxs = "c:\\projects\\PingIdentity\\testing\\testplans";

	private static final String pathToLogs = "c:\\projects\\PingIdentity\\testing\\Logs\\CE";

	private static final String pathToUpdatedJmxs = "c:\\worked\\updated";

	public static void main(String[] args) throws Exception {
		System.out.println("----next iteration");
		File workingDir = new File(LogUpdater.pathToJmxs);
		File[] jmxFilesList = workingDir.listFiles();
	String logConstName = "";
		//SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		//DefaultHandler handler = new JmxXmlHandler();
		DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory
				.newInstance();
		docBuildFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();

		for (int i = 0, max = jmxFilesList.length; i < max; i++) {
			//if (i == 1)
			//				break;
			if (!jmxFilesList[i].isDirectory()) {
				System.out.println("***********processing for : "
						+ jmxFilesList[i].getName());

				//parser.parse(jmxFilesList[i], handler);
				Document currentDoc = docBuilder.parse(jmxFilesList[i]);
				//currentDoc.
				NodeList currentList = currentDoc
						.getElementsByTagName("testelement"); // get ALL testelm
															  // elms
				System.out.println("length of suspicious testelms : "
						+ currentList.getLength());
				int collectorIndex = 1;
				for (int j = 0, max1 = currentList.getLength(); j < max1; j++) { // cycle
																				 // for
																				 // all
																				 // testelm
																				 // elms

					System.out.println("number of current testelm is : " + j);
					Node tmpNode = currentList.item(j); //current testelm elm
					NamedNodeMap currMap = tmpNode.getAttributes(); //attrs for
																	// current
																	// testelm
																	// elm
					boolean isResultCollector = false;
					System.out.println("length of curr testelm attr list : "
							+ currMap.getLength());
					for (int k = 0, max2 = currMap.getLength(); k < max2; k++) {
						System.out.println(k + "-i nodeName : "
								+ currMap.item(k).getNodeName());
						System.out.println(k + "-i nodeValue : "
								+ currMap.item(k).getNodeValue());
						if (currMap.item(k).getNodeName().equals("class")
								&& currMap
										.item(k)
										.getNodeValue()
										.equals(
												"org.apache.jmeter.reporters.ResultCollector")) {
							System.out.println("Collector finded!");
							isResultCollector = true;
							break;
						}
					}
					if (isResultCollector) {
						System.out.println("isCollector!");
						NodeList testElmChilds = tmpNode.getChildNodes();
						System.out
								.println("number of childs in searched testelm : "
										+ testElmChilds.getLength());
						Node searchedNode = getNodeByAttr(testElmChilds,
								"property", "name", "filename");
						searchedNode.setTextContent(LogUpdater.pathToLogs
								+ "\\" + remExt(jmxFilesList[i].getName())
								+ "_CE" + ".log");
						++collectorIndex;
						System.out.println("setText");
					}

				}
				//currentDoc.
				saveXml(currentDoc, jmxFilesList[i].getName());
			}
			//System.out.println(jmxFilesList[i]);
		}

		System.out.println("end");
	}

	private static Node getNodeByAttr(NodeList list, String nodeName,
			String attrName, String attrValue) {
		Node result = null;
		boolean isFinded = false;

		for (int i = 0, max = list.getLength(); i < max; i++) {
			if (!isFinded) {
				Node currNode = list.item(i);
				System.out.println("curr childnode name : "
						+ currNode.getNodeName());
				System.out.println("curr childnode localname : "
						+ currNode.getLocalName());
				System.out.println("curr childnode value : "
						+ currNode.getNodeValue());
				System.out.println("curr childnode textcontent : "
						+ currNode.getTextContent());
				if (currNode.getNodeName().equals(nodeName)) {
					NamedNodeMap currAttrs = currNode.getAttributes();
					for (int j = 0, max1 = currAttrs.getLength(); j < max1; j++) {
						Node tmpAttr = currAttrs.item(j);
						if (tmpAttr.getNodeName().equals(attrName)) {
							if (tmpAttr.getNodeValue().equals(attrValue)) {
								System.out.println("proc find!");
								isFinded = true;
								result = currNode;
								break;
							}
						}
					}
				}
			}
		}

		return result;
	}

	private static String remExt(String name) {
		String result = null;

		int pointIndex = name.lastIndexOf(".");
		result = name.substring(0, pointIndex);
		System.out.println("wo ext filename : " + result);
		return result;
	}

	private static void saveXml(Document doc, String fileName) {
		DOMSource domSource = new DOMSource(doc);
		File xml = new File(LogUpdater.pathToUpdatedJmxs + "\\" + fileName);
		StreamResult streamRes = new StreamResult(xml);
		try {
			TransformerFactory.newInstance().newTransformer().transform(
					domSource, streamRes);
		} catch (TransformerConfigurationException tce) {
			tce.printStackTrace();
		} catch (TransformerException te) {
			te.printStackTrace();
		}
	}
}