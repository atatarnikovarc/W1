package testscript.impl;

import java.util.Vector;
import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import testscript.TestScript;
import testscript.TestScriptContainer;
import testscript.TestStep;

import util.Config;

import xml.TEntityResolver;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.XMLFilterImpl;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.Header;

public class IeInspector implements TestScriptContainer {
	private Vector testscripts;

	private int testScriptNumber;

	public IeInspector(String folderName) throws Exception {
		this.testscripts = new Vector();
		processTestScripts(folderName);
		this.testScriptNumber = testscripts.size();
	}

	public TestScript getTestScript(int index) {
		return (TestScript) this.testscripts.get(index);
	}

	public int getTestScriptNumber() {
		return this.testScriptNumber;
	}

	private void processTestScripts(String folderName) throws Exception {
		// String testScriptFolderName = new String("folder"); //TODO : will get
		// from global Config class instance
		File scriptFolder = new File(folderName); // TODO : FilenameFilter
													// should be used here to
													// get only xml files
		String[] testScriptsNames = scriptFolder.list();

		for (int i = 0; i < testScriptsNames.length; i++) {
			Document idoc = parseTestScript(folderName + "\\"
					+ testScriptsNames[i]);
			this.testscripts.add(buildTestScript(idoc, testScriptsNames[i]));
			makeIoOps(idoc, folderName + "\\" + testScriptsNames[i]);
		}
	}

	private void makeIoOps(Document doc, String tsname) throws Exception {
		removeFile(tsname);
		if ("yes"
				.equalsIgnoreCase(Config.getInstance().getIsResizeTestScript()))
			resizeXml(doc, tsname);
	}

	private void removeFile(String name) {
		File ftr = new File(name);
		ftr.delete();
	}

	private void resizeXml(Document doc, String filename) throws Exception {
		Document filteredDoc = removeNodes(doc);

		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.transform(new DOMSource(filteredDoc), new StreamResult(
				new FileOutputStream(filename)));
	}

	private Document removeNodes(Document doc) {

		// TODO : more appropriate approaches could be the following : recursion
		// or DOM filtering during parsing
		String[] elementNameTR = { "timestart", "timeend", "duration",
				"processname", "result", "size", "stage", "mimetype",
				"redirecturl", "requestCamefromCache", "responseCamefromCache",
				"requestobjectname", "winet_sr_result",
				"winet_sr_errormessage", "bodySize", "content", "cookies",
				"cache", "QueryString", "stream" };
		Document pdoc = doc;

		NodeList entrylist = pdoc.getElementsByTagName("entry");

		for (int i = 0; i < entrylist.getLength(); i++) {
			NodeList entrychilds = entrylist.item(i).getChildNodes();
			for (int j = 0; j < entrychilds.getLength(); j++) {
				Node currentChild = entrychilds.item(j);
				String currentChildName = currentChild.getNodeName();
				if (currentChildName.equalsIgnoreCase(elementNameTR[0])
						|| currentChildName.equalsIgnoreCase(elementNameTR[1])
						|| currentChildName.equalsIgnoreCase(elementNameTR[2])
						|| currentChildName.equalsIgnoreCase(elementNameTR[3])
						|| currentChildName.equalsIgnoreCase(elementNameTR[4])
						|| currentChildName.equalsIgnoreCase(elementNameTR[5])
						|| currentChildName.equalsIgnoreCase(elementNameTR[6])
						|| currentChildName.equalsIgnoreCase(elementNameTR[7])
						|| currentChildName.equalsIgnoreCase(elementNameTR[8])
						|| currentChildName.equalsIgnoreCase(elementNameTR[9])
						|| currentChildName.equalsIgnoreCase(elementNameTR[10])
						|| currentChildName.equalsIgnoreCase(elementNameTR[11])
						|| currentChildName.equalsIgnoreCase(elementNameTR[12])
						|| currentChildName.equalsIgnoreCase(elementNameTR[13])
						|| currentChildName.equalsIgnoreCase(elementNameTR[14])
						|| currentChildName.equalsIgnoreCase(elementNameTR[15])
						|| currentChildName.equalsIgnoreCase(elementNameTR[16])
						|| currentChildName.equalsIgnoreCase(elementNameTR[17])
						|| currentChildName.equalsIgnoreCase(elementNameTR[18])
						|| currentChildName.equalsIgnoreCase(elementNameTR[19])) {
					entrylist.item(i).removeChild(currentChild);
				}
			}
		}
		return pdoc;
	}

	private Document parseTestScript(String filename) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setEntityResolver(new XMLFilterImpl());
		return builder.parse(filename);
	}

	private TestScript buildTestScript(Document doc, String scriptName) {

		NodeList list = doc.getElementsByTagName("entry"); // IeInspector
															// contains each
															// http-request into
															// 'entry' xml elm

		String method;
		String url;
		Vector requestHeaders;
		Vector postParams = null;

		Vector testSteps = new Vector(); // steps to create TestScript
											// instance

		// all 'entry' elements - one 'entry' elm - one request\step
		for (int i = 0; i < list.getLength(); i++) {
			// get current 'entry' element
			Node node = list.item(i);

			NamedNodeMap attrMap = node.getAttributes();

			// get url and method properties
			method = new String(attrMap.getNamedItem("method").getNodeValue());
			url = new String(attrMap.getNamedItem("url").getNodeValue());

			// navigate to needed
			// nodes////////////////////////////////////////////////
			NodeList child_of_entry_with_headers_name = ((Element) node)
					.getElementsByTagName("headers");
			Node headers_node = child_of_entry_with_headers_name.item(0);
			NodeList child_of_headers = ((Element) headers_node)
					.getElementsByTagName("requestheaders");
			Node request_header = child_of_headers.item(0);
			NodeList headers = ((Element) request_header)
					.getElementsByTagName("header");
			// /////////////////////////////////////////////////////////////////////////

			// get all headers of request
			requestHeaders = new Vector();
			for (int j = 0; j < headers.getLength(); j++)
				requestHeaders
						.add(new String(headers.item(j).getTextContent()));

			// in a case request is POST method
			if (attrMap.getNamedItem("method").getNodeValue().equals("POST")) {
				// entry --> PostData --> post params
				NodeList postDataList = ((Element) node)
						.getElementsByTagName("PostData");
				Node postDataNode = postDataList.item(0);
				NodeList paramsList = ((Element) postDataNode)
						.getElementsByTagName("params");

				if (paramsList != null) { // sometimes POST request doesn't
											// contain parms - possible -
											// excessive condition checking
					Node paramsNode = paramsList.item(0);
					if (paramsNode != null) {// sometimes POST request
												// doesn't contain parms
						NodeList paramList = ((Element) paramsNode)
								.getElementsByTagName("param");

						postParams = new Vector();
						for (int k = 0; k < paramList.getLength(); k++) {
							NamedNodeMap nameAttrMap = paramList.item(k)
									.getAttributes();
							// TODO : it seems Vector is not suitable structure
							// for post params processing
							postParams.add(new Header(nameAttrMap.getNamedItem(
									"name").getNodeValue(), paramList.item(k)
									.getTextContent()));
						}
					}
				}
			}

			testSteps.add(new TestStep(buildRequest(method, url,
					processHeaders(requestHeaders), postParams)));
		} // all 'entry' elemetns cycle

		return new TestScript(scriptName, testSteps);
	}

	private HttpMethod buildRequest(String method, String url, Vector headers,
			Vector postParams) {

		if (method.equals("GET"))
			return buildGetRequest(url, headers);
		else if (method.equals("POST"))
			return buildPostRequest(url, headers, postParams);
		else
			return null; // TODO : make more clever processing
	}

	private HttpMethod buildGetRequest(String url, Vector headers) {
		HttpMethod method = new GetMethod(url);
		Header header = null;
		for (int i = 0; i < headers.size(); i++) {
			header = (Header) headers.get(i);
			method.addRequestHeader(header.getName(), header.getValue());
		}
		return method;
	}

	private HttpMethod buildPostRequest(String url, Vector headers,
			Vector params) {
		HttpMethod method = new PostMethod(url);
		Header header = null;
		for (int i = 0; i < headers.size(); i++) {
			header = (Header) headers.get(i);
			method.addRequestHeader(header.getName(), header.getValue());
		}

		if (params != null) {
			for (int i = 0; i < params.size(); i++) {
				header = (Header) params.get(i);
				((PostMethod) method).addParameter(header.getName(), header
						.getValue());
			}
		}
		return method;
	}

	private Vector processHeaders(Vector headers) {
		// remove Cookie header - done
		// remove Host header - done
		// remove first header - done
		// split each header to name\value pair

		headers.remove(0); // remove request line header - it is not needed to
							// create http request
		Vector processedHeaders = new Vector();

		// parse headers strings
		for (int i = 0; i < headers.size(); i++) {
			String name = ((String) headers.get(i)).substring(0,
					(((String) headers.get(i)).indexOf(":")));
			String value = ((String) headers.get(i)).substring(
					(((String) headers.get(i)).indexOf(":") + 1),
					((String) headers.get(i)).length());
			processedHeaders.add(new Header(name, value));
		}

		// remove excessive properties
		for (int i = 0; i < processedHeaders.size(); i++)
			if (((Header) processedHeaders.get(i)).getName().equals("Host")
					|| ((Header) processedHeaders.get(i)).getName().equals(
							"Cookie"))
				processedHeaders.remove(i);

		return processedHeaders;
	}
}
