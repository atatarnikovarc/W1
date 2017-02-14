package testscript;

import org.apache.commons.httpclient.HttpMethod;

public class TestStep {
	private HttpMethod method;
		
	public TestStep(HttpMethod method) {
		this.method = method;
	}

	public HttpMethod getMethod() { return this.method; }
}
