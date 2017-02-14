/*
 * Created on Jul 16, 2005
 */

package lumber;


public class AnalyzerDescriptor {
	private String assertionRef;
	private String ceClass;
	private String fwClass;
	
	public AnalyzerDescriptor(String assertionRef, String ceClass, String fwClass) {
		this.assertionRef = assertionRef;
		this.ceClass = ceClass;
		this.fwClass = fwClass;
	}
	
		
	public String getAssertionRef() {
		return assertionRef;
	}
	public String getCeClass() {
		return ceClass;
	}
	public String getFwClass() {
		return fwClass;
	}
}
