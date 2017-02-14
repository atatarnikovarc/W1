/*
 * Created on Jul 15, 2005
 *  
 */

package lumber;

import java.util.Hashtable;



public class AnalyzerDescriptorsBuilder {
	private Hashtable descriptors;

	public AnalyzerDescriptorsBuilder(String filename) {
		descriptors = new Hashtable();
	}

	public AnalyzerDescriptor getByName(String name) {
		return (AnalyzerDescriptor) descriptors.get(name);
	}
}