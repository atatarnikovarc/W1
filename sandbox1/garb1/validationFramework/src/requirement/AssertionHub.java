/*
 * Created on Jul 26, 2005
 */

package requirement;

import java.util.Map;
import java.lang.reflect.Constructor;

public class AssertionHub {
	private String assertionName;
	private Map    parms;
	private Constructor constructor;
	private boolean result;
	private String  ceClassName;
		
	public AssertionHub(String assertionName, Map parms, Constructor constructor,
			            boolean result, String ceClassName) {
		this.assertionName = assertionName;
		this.parms = parms;
		this.constructor = constructor;
		this.result = result;
		this.ceClassName = ceClassName;
	}
		
	public String getAssertionName() {
		return assertionName;
	}
	public String getCeClassName() {
		return ceClassName;
	}
	public Constructor getConstructor() {
		return constructor;
	}
	public Map getParms() {
		return parms;
	}
	
	public boolean getResult() {
		return result;
	}
	
	public void setResult(boolean value) {
		this.result = value;
	}
}
