/*
 * Created on Oct 21, 2005
 */

package config;


import java.lang.reflect.Constructor;

public class AssertionDescriptor {
	private String ceClass;
	private String name;
	private Constructor constructor;
	
	public AssertionDescriptor(String name, String ceClass, Constructor constructor) {
		this.name = name;
		this.ceClass = ceClass;
		this.constructor = constructor;
	}
	
	public String getCeClass() {
		return ceClass;
	}
	
	public Constructor getConstructor() {
		return constructor;
	}
	
	public String getName() {
		return name;
	}
}
