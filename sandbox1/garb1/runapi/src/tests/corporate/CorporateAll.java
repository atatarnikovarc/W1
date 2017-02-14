package tests.corporate;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CorporateAll extends TestCase {
	public CorporateAll() {
		super();
	}
	
    public static Test suite() {
	  TestSuite suite = new TestSuite();
      suite.addTestSuite(COMMON_AUTH.class);
      return suite;
    }
}
