/*
 * Created on Jul 27, 2005
 */

package test.experiment;

import java.util.Properties;
import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Hashtable;

public class runner {
	private final static Map k = new Hashtable();
	
	static {
		k.put("test", ClassToRun.class);
	};
	
	public static void main(String[] args) {
		Properties clProps = new Properties();
		clProps.put("classs", "runner.class");
		Properties props = System.getProperties();
		System.out.println(props.get("java.class.path"));
		File f = new File(".");
		System.out.println(f.getAbsolutePath());
		try {
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			//cl.loadClass("runner.class");
			Class clazz = Class.forName("test.experiment.ClassToRun");
			System.out.println(clazz.toString());
			System.out.println(k.get("test").toString());
			Class classs = (Class) k.get("test");
			Constructor construct = classs.getConstructor(new Class[]{int.class});
			ClassToRun classTR = (ClassToRun) construct.newInstance(new Object[]{new Integer(312)});
			System.out.println(classTR.getI());
			Constructor zzconstructor = clazz.getConstructor(new Class[]{int.class});
			ClassToRun classTR1 = (ClassToRun) zzconstructor.newInstance(new Object[]{new Integer(123)});
			System.out.println(classTR1.getI());
			//Constructor[] constr = ((Class)clProps.get("classs")).getConstructors();
		} catch (ClassNotFoundException cne) {
			cne.printStackTrace();
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		} catch (java.lang.reflect.InvocationTargetException ite) {
			ite.printStackTrace();
		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
		} catch (InstantiationException ie) {
			ie.printStackTrace();
		}
		
	}
}
