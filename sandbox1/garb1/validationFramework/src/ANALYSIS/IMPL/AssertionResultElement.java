/*
 * Created on Aug 12, 2005
 */

package analysis.impl;


public class AssertionResultElement {
	private String assertionName;
	private boolean result;
	
	public AssertionResultElement(String name, boolean result) {
		this.assertionName = name;
		this.result = result;
	}
		
	public String getAssertionName() {
		return assertionName;
	}
	
	public boolean getResult() {
		return result;
	}
}
