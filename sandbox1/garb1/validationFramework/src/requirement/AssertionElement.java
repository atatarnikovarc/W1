/*
 * Created on Aug 10, 2005
 */

package requirement;

import java.util.Map;
import java.util.Hashtable;

//this class describes assertion which is consist of assertion name and its parameters
//it is used for transformation of lists of framework model

public class AssertionElement {
	private String name; //assertion name
	private Map parms; // assertion parameters
	
	public AssertionElement(String name, Map parms) {
		parms = new Hashtable(parms);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Map getParms() {
		return parms;
	}
}
