/*
 * Created on Nov 6, 2005
 */

package test.VF.unit_test;

import junit.framework.*;


public class MaterTest extends TestCase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		TestSuite result = new TestSuite();
		result.addTest(new TestSuite(MappingControllerTester.class));
		result.addTest(new TestSuite(MappingModelTester.class));
		return result;
	}

}
