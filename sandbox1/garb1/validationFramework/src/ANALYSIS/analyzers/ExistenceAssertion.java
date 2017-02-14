/*
 * Created on Jul 5, 2005
 */

package analysis.analyzers;

import analysis.Analyzer;
import java.util.Hashtable;
import org.w3c.dom.Document;


public class ExistenceAssertion extends Analyzer {
	//hardcoded names for parms names which describes assertion
	private final static String VARIABLE_PARM_NAME = "VarPath";
	private final static String COUNT_PARM_NAME = "Count";
		
	public ExistenceAssertion(Document document, Hashtable parms, String ceClassName) {
		super(document, parms, ceClassName);
	}
	
	public boolean analyze() {
		//1. to find element with appropriate class
		//2. to go through via all vars in choosen class and comparing them with
		// storage (xmlExctractor)
		
		return false;
	}
}
