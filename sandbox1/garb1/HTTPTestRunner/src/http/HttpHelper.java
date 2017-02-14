package http;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpClient;

public class HttpHelper {
	public static HttpMethod buildRequest() {
		return null;
	}
	
	public static HttpResult executeRequest(HttpMethod method) throws Exception {
		HttpClient client = new HttpClient();
		int code = client.executeMethod(method);
		return new HttpResult(code, method.getResponseBodyAsString());
	}
}
