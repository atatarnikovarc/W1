/*
 * Created on Jul 5, 2005
 */

package analysis;

import org.w3c.dom.Document;
import java.util.Hashtable;
import java.util.Map;

public abstract class Analyzer {
	private Document document;
	private Hashtable parms;
	private String ceClassname;
	
	public Analyzer(Document document, Hashtable parms, String ceClassname) {
		this.parms = parms;
		this.document = document;
		this.ceClassname = ceClassname;
	}
	
	public abstract boolean analyze();
	
	protected String get(String key) {
		return (String) parms.get(key);
	}
	
	protected Document getDocument() {
		return this.document;
	}
	
	protected Map getParms() {
		return this.parms;
	}
	
	protected String getCeClassName() {
		return this.ceClassname;
	}
}
