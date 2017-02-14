package core;

import testscript.impl.IeInspector;
import testscript.HttpTestCase;

import util.Config;

import java.util.Vector;

public class RunAllTests {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		IeInspector testScripts = new IeInspector(Config.getInstance().getTestScriptFolder());
		Vector results = new Vector();
		
		for (int i = 0; i < testScripts.getTestScriptNumber(); i++) {
			HttpTestCase testCase = new HttpTestCase(testScripts.getTestScript(i));
			testCase.testRunTestScript();
			results.add(new EndResult(testCase.getResult(), testCase.getResultFlag(), testScripts.getTestScript(i).getName()));
		}
		
		writeResultsToDisk(results);
	}

	private static void writeResultsToDisk(Vector results) {
		
	}
}

final class EndResult {
	private Vector results;
	private boolean isPassed;
	private String name;
	
	public EndResult(Vector results, boolean passed, String name) {
		this.results = results;
		this.isPassed = passed;
		this.name = name;
	}
	
	public Vector getResults() { return this.results; }
	
	public boolean getPassed() { return this.isPassed; }
	
	public String getName() { return this.name; }
}
