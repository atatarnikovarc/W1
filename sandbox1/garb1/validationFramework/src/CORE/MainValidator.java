/*
 * Created on Jul 2, 2005
 *
 */

package core;

import java.io.IOException;

import jxl.write.WriteException;
import mapping.MappingController;
import xml.DomStorage;
import analysis.ImplementationChecker;
import analysis.impl.AnalysisResult;
import config.ConfigXmlReader;

public class MainValidator {
	
	public MainValidator() throws WriteException, IOException {
		run();
	}
	
	private void run() throws WriteException, IOException {
		int mappingsSize = ConfigXmlReader.getInstance().getMappingsSize();
		for (int i = 0, max1 = mappingsSize; i < max1; i++) {
			MappingController mappingController = new MappingController(ConfigXmlReader.getInstance().getMappingByIndex(i));
			DomStorage documentsStorage = new DomStorage(mappingController.getTestPlans());
			int j = 0;
			while (true) {
				//TODO : investigate a condition of empty cell where requirement is located
				// it seems - one parameter of total amount of requirements is necessary
				if (mappingController.getReqByIndex(j) == null &&
						mappingController.getReqByIndex(j) == "")
					break;
				if (mappingController.isPresent(j)) {
					System.out.println("before impchecker");
					ImplementationChecker implChecker = new ImplementationChecker(mappingController.getReqByIndex(j));
					for (int k = 0, max2 = mappingController.getTPCount(); k < max2; k++) {
						if (mappingController.isMappedWith(j, k)) {
							AnalysisResult result = implChecker.analyze(documentsStorage.getByName(mappingController.getTPByIndex(k)));
							//TODO : add result of check in the report
							System.out.println("mapped");
						} 
					} 
				} // if current requirement maps with test plans
				++j;
			} // current mapping cycle
			mappingController.saveResult();
		} // requirements mappings cycle 
	} // run()
	
	public static void main(String[] args) throws WriteException, IOException {
		new MainValidator();
	} 
}
