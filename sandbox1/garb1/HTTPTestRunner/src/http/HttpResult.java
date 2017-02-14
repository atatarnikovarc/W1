package http;

// class represents bean to fetch response result

public class HttpResult {
	private int code;
	private String responseBody;

	public HttpResult(int code, String body) {
		this.code = code;
		responseBody = new String(body);
	}
	
	public int getCode() { return this.code; }
	
	public String getResponseBody() { return this.responseBody; }
}
