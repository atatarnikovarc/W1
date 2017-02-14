/*
 * Created on Aug 4, 2005
 */

package test.experiment;


import java.lang.reflect.Constructor;
import java.io.File;
import java.util.Properties;

public class runner_1 {

	public static void main(String[] args) throws Exception {
		try {
			
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			System.out.println("!");
			File file = new File(".");
			System.out.println(file.getAbsolutePath());
			System.out.println(file.getCanonicalPath());
			cl.loadClass("runner");
			Properties props1 = System.getProperties();
			System.out.println(props1.get("java.class.path"));
			Class clazz = Class.forName("ClassToRun");
			Constructor[] constructor = clazz.getConstructors();
			Constructor constr = clazz.getConstructor(new Class[]{int.class});
			//ClassToRun ctr1 = (ClassToRun) constr.newInstance(23);
			//System.out.println(ctr1.getI() + "  fff");
			//int.class.getClass();
			System.out.println(constructor.length);
			System.out.println(constructor[0]);
			
			//ClassToRun ctr = (ClassToRun)constructor[0].newInstance(Integer.parseInt("1"));
			//ClassToRun ctr = (ClassToRun)constructor[0].newInstance(1);
			//System.out.println(ctr.getI());
			//ClassToRun ctr = (ClassToRun)clazz.newInstance();
			System.out.println("!");
			//System.getProperties().list(System.out);
		} catch (ClassNotFoundException cne) {
			cne.printStackTrace();
		}/* catch (InstantiationException ie) {
			ie.printStackTrace();
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		} catch (java.lang.reflect.InvocationTargetException ite) {
			ite.printStackTrace();
		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
		}*/
	}
}
