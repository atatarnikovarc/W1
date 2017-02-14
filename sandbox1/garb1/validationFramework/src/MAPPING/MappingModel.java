/*
 * Created on Jul 16, 2005
 *
 */

package mapping;

import jxl.*;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;

import java.io.File;
import java.io.IOException;

import config.Config;
import util.CalendarHelper;

public class MappingModel {
	private WritableWorkbook workbook;
	private WritableSheet sheet;
	
	public MappingModel(String filename, String worksheet) {
		try {
			Workbook book = Workbook.getWorkbook(new File(Config.getInstance().getDataDir() + "\\" + filename));
			String currentDate = CalendarHelper.getCurrentDate();  
			File reportfolder = new File(Config.getInstance().getResultsDir() + "\\" + "report" + "-" + currentDate);
			reportfolder.mkdir();
			String Ufilename = filename.substring(0, filename.indexOf("."));
			this.workbook = Workbook.createWorkbook(new File(reportfolder.getAbsolutePath() + "\\" + Ufilename + "-" + "report" + "-" + currentDate + ".xls"), book);
			this.sheet = workbook.getSheet(worksheet);
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (BiffException be) {
			be.printStackTrace();
		}
	}
	
	public void setCellContent(int x, int y, String content) {
		Label label = new Label(x, y, content);
		try {
			sheet.addCell(label);
		} catch (RowsExceededException ree) {
			ree.printStackTrace();
		} catch (WriteException we) {
			we.printStackTrace();
		}
	}
	
	public void setCellComment(int x, int y, String comment) {
		
	}
	
	public void setCellColor(int x, int y, Colour color) {
		try {
			if (sheet.getCell(x, y).getType() == CellType.EMPTY) {
				//Label label = new Label();
				WritableCellFormat f = new WritableCellFormat();
				f.setBackground(color);
				//TODO: add correct code here
				//WritableCell ws = new WritableCell();
				//sheet.addCell();
			} else {
				WritableCell c = sheet.getWritableCell(x, y);
				WritableCellFormat newFormat = new WritableCellFormat(c.getCellFormat());
				newFormat.setBackground(color);
				c.setCellFormat(newFormat);
				//whether here is need workbook.write()?
			}
		} catch (WriteException we) {
			we.printStackTrace();
		}
	}
	
	public void updateCellComment(int x, int y, String comment ) {
		
	}
	
	public String getCellContent(int x, int y) {
		if (sheet.getCell(x, y).getType() == CellType.EMPTY) {
			System.out.println("empty cell");
			return null; 
		}
		return sheet.getCell(x, y).getContents();
	}
	
	public String getCellComment(int x, int y) {
		if (sheet.getCell(x, y).getType() == CellType.EMPTY)
			return null; 
		return sheet.getCell(x, y).getCellFeatures().getComment();
	}
	
	public Colour getCellColor(int x, int y) {
		if (sheet.getCell(x, y).getType() == CellType.EMPTY)
			return null; 
		return sheet.getCell(x, y).getCellFormat().getBackgroundColour();
	}
	
	//TODO: wrong format of saved Excel document - need to be investigate
	public void save() throws IOException, WriteException {
		workbook.write();
		workbook.close();
	}
}
