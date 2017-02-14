package main;

import tests.corporate.CORP_ADMIN_PWD_CHANGE;
import tests.corporate.CORP_ADMIN_PWD_GET;
import tests.corporate.CORP_ADMIN_SET_INFO;
import tests.corporate.COMMON_AUTH;
import junit.framework.Test;
import junit.framework.TestSuite;

//из этого класса запускаютс€ все имеющиес€ тесты

public class RunAll {
  public static Test suite() {
	  TestSuite suite = new TestSuite();
	  suite.addTestSuite(COMMON_AUTH.class);
	  suite.addTestSuite(CORP_ADMIN_SET_INFO.class);
	  suite.addTestSuite(CORP_ADMIN_PWD_CHANGE.class);
	  suite.addTestSuite(CORP_ADMIN_PWD_GET.class);
	  return suite;
  }
  
  public static void main(String[] args) {
	  junit.textui.TestRunner.run(suite());
  }
}
