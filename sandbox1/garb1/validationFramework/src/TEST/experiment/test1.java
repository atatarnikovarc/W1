/*
 * Created on Jul 29, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package test.experiment;

import java.util.Properties;

/**
 * @author atatarnikov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class test1 {

	public static void main(String[] args) {
		Properties props = System.getProperties();
		System.out.println(props.get("java.class.path"));
	}
}
