/*
 * Created on Aug 10, 2005
 */

package requirement;

// Requirement == AsstnName := parmname="parmvalue"&parmname="parmvalue"; 
//                AsstnName := parmname="parmvalue"&parmname="parmvalue"  
public class AssertionPresenter {
	public static final String ASSERTION_PARMS_DELIMITER = ":=";
	public static final String PARM_PARM_DELIMITER = "&";
	public static final String ASSERTION_ASSERTION_DELIMITER = ";";
	public static final String PARMNAME_PARMVALUE_DELIMITER = "=";
	public static final String PARMVALUE_FRAME_SYMBOL = "\"";
	public static final String EMPTINESS = "";
}
