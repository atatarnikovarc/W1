/*
 * Created on Jul 5, 2005
 */

package requirement;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Map;
import java.util.Hashtable;

//this class receives String-representation of requirement and parses it 
//in list of AssertionElement objects

public class Requirement {
	private List assertions;
	
	public Requirement(String rawRequirement) {
		assertions = new Vector();
		process(rawRequirement);
	}
	
	private void process(String req) {
		StringTokenizer tokenizer = new StringTokenizer(req, AssertionPresenter.ASSERTION_ASSERTION_DELIMITER);
		
		while(tokenizer.hasMoreTokens()) {
			String asstnToken = tokenizer.nextToken();
			StringTokenizer asstnTokenizer = new StringTokenizer(asstnToken, AssertionPresenter.ASSERTION_PARMS_DELIMITER);
			String asstnName = asstnTokenizer.nextToken();
			StringTokenizer parmsTokenizer = new StringTokenizer(asstnTokenizer.nextToken(), AssertionPresenter.PARM_PARM_DELIMITER);
			Map parms = new Hashtable();
			while (parmsTokenizer.hasMoreTokens()) {
				String parmPair = parmsTokenizer.nextToken();
				StringTokenizer pairTokenizer = new StringTokenizer(parmPair, AssertionPresenter.PARMNAME_PARMVALUE_DELIMITER);
				String parmName = pairTokenizer.nextToken();
				String parmValue = pairTokenizer.nextToken().replace(AssertionPresenter.PARMVALUE_FRAME_SYMBOL, AssertionPresenter.EMPTINESS);
				parms.put(parmName, parmValue);
			}
			assertions.add(new AssertionElement(asstnName, parms));
		}
	}
	
	public AssertionElement getAssertion(int number) {
		return (AssertionElement)assertions.get(number);
	}
	
	public List getAssertions() {
		return assertions;
	}
}
