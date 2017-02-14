/*
 * Created on Jul 4, 2005
 *
 */

package mapping;

import analysis.impl.AnalysisResult;
import jxl.write.WriteException;
import java.io.IOException;

import lumber.MappingCoordinator;
import lumber.MappingDescriptor;

import mappinglist.*; 



public class MappingController {
	private MappingModel mappingModel;
	private Maptype mappingDescriptor;
	//private MappingCoordinator coordinator;
	
	public MappingController(Maptype descriptor) {
		mappingDescriptor = descriptor;
		mappingModel  = new MappingModel(mappingDescriptor.getFilename(), mappingDescriptor.getWorksheet());
		//coordinator = new MappingCoordinator(descriptor);
	}
	
	public boolean isPresent(int reqNumber) {
		return mappingModel.getCellContent(Integer.parseInt(mappingDescriptor.getPresentcolumn()), 
				Integer.parseInt(mappingDescriptor.getFirstrequirementY()) + reqNumber).equals("1");
	}
	
	public boolean isMappedWith(int reqNumber, int tpNumber) {
		boolean result = false;
		
		return result;
	}
	
	public String getTPByIndex(int tpIndex) {
		return mappingModel.getCellContent(Integer.parseInt(mappingDescriptor.getLefttestplanX()) + tpIndex, 
				Integer.parseInt(mappingDescriptor.getLefttestplanY()));
	}
	
	public String getReqByIndex(int reqIndex) {
		return mappingModel.getCellContent(Integer.parseInt(mappingDescriptor.getFirstrequirementX()), Integer.parseInt(mappingDescriptor.getFirstrequirementY()) + reqIndex);
	}
	
	public int getTPCount() {
		return Math.abs(Integer.parseInt(mappingDescriptor.getLefttestplanX()) - Integer.parseInt(mappingDescriptor.getRighttestplanX())) + 1;
	}
	
	public String[] getTestPlans() {
		System.out.println(getTPCount());
		String[] result = new String[getTPCount()];
		
		for (int i = 0, max1 = result.length; i < max1; i++) 
			result[i] = mappingModel.getCellContent(Integer.parseInt(mappingDescriptor.getLefttestplanX()) + i, Integer.parseInt(mappingDescriptor.getLefttestplanY()));
				
		return result;
	}
	
	public void markWithResult(int reqIndex, int tpIndex, AnalysisResult analysisRes) {
		
	}
	
	private void addComment(int reqIndex, int tpIndex, String comment) {
		
	}
	
	public void saveResult() throws WriteException, IOException {
		mappingModel.save();
	}
}
