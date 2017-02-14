/*
 * Created on Jul 26, 2005
 */

package analysis;

import mapping.MappingRepresentator;

import org.w3c.dom.Document;

import config.AssertionDescriptor;
import config.ConfigXmlReader;

import requirement.AssertionHub;
import requirement.Requirement;
import requirement.AssertionElement;
import analysis.impl.AssertionResultElement;


import java.util.List;
import java.util.Vector;
import java.lang.reflect.InvocationTargetException;
import analysis.impl.AnalysisResult;

public class ImplementationChecker {
	private Document document;
	private List assertionHubs; //contains 'AssertionHub' element
	
	public ImplementationChecker(String rawRequirement) {
		assertionHubs = new Vector();
		process(rawRequirement);
	}
	
	private void process(String requirement) {
		prepareList(new Requirement(requirement).getAssertions());
	}
	
	private void setDocument(Document document) {
		this.document = document;
	}
	
	public AnalysisResult analyze(Document document) {
		setDocument(document);
				
		for (int i = 0, max1 = assertionHubs.size(); i < max1; i++) {
			AssertionHub hub = (AssertionHub) assertionHubs.get(i);
			try {
				boolean result = ((Analyzer)hub.getConstructor().newInstance(new Object[]{document, hub.getParms(), hub.getCeClassName()})).analyze();
				hub.setResult(result);
				//TODO: need to check wether result is updated in list or not
			} catch (InstantiationException ie) {
				ie.printStackTrace();
			} catch (IllegalAccessException iae) {
				iae.printStackTrace();
			} catch (InvocationTargetException ite) {
				ite.printStackTrace();
			}
		}
		
		AnalysisResult result = getAnalysisResult();
		resetResults();
		return result;
	}
	
	private void prepareList(List rawlist) {
		for (int i = 0, max1 = rawlist.size(); i < max1; i++) {
			AssertionElement asstnElm = (AssertionElement)rawlist.get(i);
			AssertionDescriptor ad = (AssertionDescriptor) ConfigXmlReader.getInstance().getAsstnDescriptor(asstnElm.getName());
			this.assertionHubs.add(new AssertionHub(asstnElm.getName(), asstnElm.getParms(), ad.getConstructor(), false, ad.getCeClass()));
		}
	}
	
	private AnalysisResult getAnalysisResult() {
		List results = new Vector(); //AssertionResultElement for each element
		int totalResult = 0;
		boolean resultFlag = false;
		int implementedIndex = 0;
		
		int i = 0;
		for (int max1 = this.assertionHubs.size(); i < max1; i++) {
			AssertionHub hub = (AssertionHub)this.assertionHubs.get(i);
			results.add(new AssertionResultElement(hub.getAssertionName(), hub.getResult()));
			if (hub.getResult()) {
				resultFlag = true;
				++implementedIndex;
			}
		}
		
		if (implementedIndex == i)
			totalResult = MappingRepresentator.FULLY_IMPLEMENTED;
		else if (resultFlag)
			totalResult = MappingRepresentator.PARTIALLY_IMPLEMENTED;
		else 
			totalResult = MappingRepresentator.NOT_IMPLEMENTED;
		
		return new AnalysisResult(totalResult, results);
	}
	
	private void resetResults() {
		for (int i = 0, max1 = this.assertionHubs.size(); i < max1; i++) {
			//TODO: need to check  - wether actually 'result' value is changed or not
			((AssertionHub)assertionHubs.get(i)).setResult(false);
		}
	}
}
