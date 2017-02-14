package core;

import testscript.impl.IeInspector;
import testscript.TestScript;
import testscript.TestStep;

import util.Config;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.NameValuePair;

public class TestRun {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		IeInspector inspector = new IeInspector(Config.getInstance().getTestScriptFolder());
		
		System.out.println("test scripts number is : " + inspector.getTestScriptNumber());
		
		for (int i = 0; i < inspector.getTestScriptNumber(); i++) {
			TestScript script = inspector.getTestScript(i); 
			System.out.println(script.getName());
			for (int j = 0; j < script.getStepsNumber(); j++) {
				System.out.println(j + " -j test step(request)");
				System.out.println("step method : " + script.getStep(j).getMethod().getName() + "  step url : " + script.getStep(j).getMethod().getURI().toString());
				Header[] headers = script.getStep(j).getMethod().getRequestHeaders();
				for (int k = 0; k < headers.length; k++) {
					System.out.println("header name : " + headers[k].getName() + "  header value : " + headers[k].getValue());
				}
				if (script.getStep(j).getMethod().getName().equals("POST")) {
					NameValuePair[] postparms = ((PostMethod)script.getStep(j).getMethod()).getParameters();
					for (int l = 0; l < postparms.length; l++) 
						System.out.println("parm name : " + postparms[l].getName() + "  parm value : " + postparms[l].getValue());
				}
			}
		}
	}
}
