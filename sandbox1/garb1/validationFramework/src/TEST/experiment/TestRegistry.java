/*
 * Created on Nov 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package test.experiment;

import java.util.prefs.Preferences;

/**
 * @author atatarnikov
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestRegistry {

	public static void main(String[] args) {
		Preferences p = Preferences.userRoot();
				System.out.println(p.name());
		System.out.println("1");
	}
}
