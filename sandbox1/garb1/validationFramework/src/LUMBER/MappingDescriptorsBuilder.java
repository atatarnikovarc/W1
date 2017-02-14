/*
 * Created on Jul 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package lumber;

import java.util.List;
import java.util.Vector;

//import vf.xml.mappings


public class MappingDescriptorsBuilder {
	private List mappings;
	
	public MappingDescriptorsBuilder(String mappingsfile) {
		mappings = new Vector();
	}
	
	public int getSize() {
		return mappings.size();
	}
	
	public MappingDescriptor getByIndex(int index) {
		return (MappingDescriptor) mappings.get(index);
	}
}
