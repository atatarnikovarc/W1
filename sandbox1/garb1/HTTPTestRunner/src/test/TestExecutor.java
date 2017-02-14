package test;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import util.Config;

public class TestExecutor extends TestCase {

	public TestExecutor(String arg0) {
		super(arg0);
	}
		
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*public static void main(String[] args) {
		junit.textui.TestRunner.run(TestExecutor.class);
	}*/
	
	public static TestSuite suite() {
		TestSuite testsuite = new TestSuite("all tests executed by TestExecutor class");
		testsuite.addTestSuite(ToBeRunTC.class);
		testsuite.addTestSuite(TestExecutor.class);
		return testsuite;
	}
	
	public void testExec() {
		assertEquals(1,2);
	}
	
	public void testConfig() {
		assertEquals(true, Config.getInstance().getTestScriptFolder());
	}
}
