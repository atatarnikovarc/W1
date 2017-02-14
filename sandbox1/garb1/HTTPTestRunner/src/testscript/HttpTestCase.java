package testscript;

import junit.framework.TestCase;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

import http.HttpResult;

import java.util.Vector;

public class HttpTestCase extends TestCase {
	private boolean isTestCasePassed;
	private HttpClient httpClient;
	private TestScript testScript;
	private Vector result;
	private HttpMethod method;
	
	public HttpTestCase(TestScript script) {
		super();
		this.httpClient = new HttpClient(); 
		this.testScript = script;
		this.result = new Vector();
	}

	protected void setUp() throws Exception {
		this.isTestCasePassed = true;
	}

	protected void tearDown() throws Exception {
		this.method.releaseConnection();
	}

	private void setTestCasePassed(boolean value) {
		this.isTestCasePassed = value;
	}
	
	public void testRunTestScript() throws Exception {
		for (int i = 0; i < this.testScript.getStepsNumber(); i++) {
			method = this.testScript.getStep(i).getMethod();
			int code = this.httpClient.executeMethod(method);
			this.result.add(new HttpResult(code, method.getResponseBodyAsString()));
			analyzeHttpResponseCode(code);
		}
		assertEquals(true, this.isTestCasePassed);
	}
	
	private void analyzeHttpResponseCode(int code) {
		//TODO: get http codes and add appropriate code analyzing
		if (code < 300)
			setTestCasePassed(true);
		else
			setTestCasePassed(false);
	}
	
	public boolean getResultFlag() { return this.isTestCasePassed; }
	
	public Vector getResult() { return this.result; }
}
