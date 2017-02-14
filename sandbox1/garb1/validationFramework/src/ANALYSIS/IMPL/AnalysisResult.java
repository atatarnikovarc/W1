/*
 * Created on Jul 4, 2005
 */
package analysis.impl;

import java.util.List;

public class AnalysisResult {
	private int totalResult;
	private List assertionsResult; //AssertionResultElement
	
	public AnalysisResult(int totalResult, List results) {
		this.totalResult = totalResult;
		this.assertionsResult = results;
	}
		
	public List getAssertionsResult() {
		return assertionsResult;
	}
	
	public int getTotalResult() {
		return totalResult;
	}
}
