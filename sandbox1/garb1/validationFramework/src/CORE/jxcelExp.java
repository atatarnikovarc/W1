/*
 * Created on Jul 8, 2005
 */

package core;

import jxl.*;
import jxl.write.*;
import java.io.*;


public class jxcelExp {
	private static final String excelPath = "c:\\projects\\PingIdentity\\pfceTestFramework\\launch\\data\\";
	private static final String excelFileName = "requirementsFormalization.xls";
	
	public static void main(String[] args) throws Exception {
		Workbook workbook = Workbook.getWorkbook(new File(jxcelExp.excelPath + jxcelExp.excelFileName));
		WritableWorkbook copy = Workbook.createWorkbook(new File("test.xls"), workbook);
		
		WritableSheet sheet = copy.getSheet(0);
		//WritableCell cell = sheet.getWritableCell(2, 6);  
	    //Label modifiedLabel = (Label) cell;
		Label cell = new Label(2, 6, "juzt to tezzt");
		//modifiedLabel.setString("juzt to tezzt");
		sheet.addCell(cell);
		copy.write();
		copy.close();
	}
}
