package test;

import junit.framework.TestCase;

public class ToBeRunTC extends TestCase {

	public ToBeRunTC(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSomeTest() {
		assertEquals(1,1);
	}
	
	public void testSomeMome() {
		assertEquals(1, 2);
	}
}
