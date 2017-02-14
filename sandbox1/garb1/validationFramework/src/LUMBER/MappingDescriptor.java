/*
 * Created on Jul 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package lumber;

/**
 * @author atatarnikov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MappingDescriptor {
	private String name;
	private String filename;
	private String worksheet;
	private String firstReqX;
	private String firstReqY;
	private String leftTPX;
	private String leftTPY;
	private String rightTPX;
	private String rightTPY;
	private String presentcolumn;
	
	public MappingDescriptor(String name, String filename, String worksheet, String firstReqX,
			       String firstReqY, String leftTPX, String leftTPY, String rightTPX, 
				   String rightTPY, String presentcolumn) {
		this.name = name;
		this.filename = filename;
		this.worksheet = worksheet;
		this.firstReqX = firstReqX;
		this.firstReqY = firstReqY;
		this.leftTPX = leftTPX;
		this.leftTPY = leftTPY;
		this.rightTPX = rightTPX;
		this.rightTPY = rightTPY;
		this.presentcolumn = presentcolumn;
	}
	public String getfilename() {
		return filename;
	}
	public String getFirstReqX() {
		return firstReqX;
	}
	public String getFirstReqY() {
		return firstReqY;
	}
	public String getLeftTPX() {
		return leftTPX;
	}
	public String getLeftTPY() {
		return leftTPY;
	}
	public String getName() {
		return name;
	}
	public String getRightTPX() {
		return rightTPX;
	}
	public String getRightTPY() {
		return rightTPY;
	}
	public String getWorkSheet() {
		return worksheet;
	}
	public String getPesentcolumn() {
		return presentcolumn;
	}
}
