package tests;

import org.w3c.dom.Document;
import util.CommonHelper;
import junit.framework.TestCase;

public class RunApiTest extends TestCase {

	public RunApiTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	protected Document getResponseDoc(String url) {
		Document result = null;

		try {
			// execute api function
			result = CommonHelper.getInstance()
					.getDocument(
							CommonHelper.getInstance().getWebGetResponse(url)
									.getText());
		} catch (Exception e) {
			System.err.println("can't get response doc: " + e);
		}
		return result;
	}

	protected String getTag(String name, Document doc) {
		return doc.getElementsByTagName(name).item(0).getTextContent();
	}
}
