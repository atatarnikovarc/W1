/*
 * Created on Nov 7, 2005
 */

package test.VF.unit_test;

import junit.framework.TestCase;
import mapping.MappingModel;

public class MappingModelTester extends TestCase {
	public void testGetCellContent() {
		MappingModel model = new MappingModel("", "");
		assertEquals(model.getCellContent(3, 10), "test value");
	}
}
