/*
 * Created on Jul 29, 2005
 */

package config;

import java.util.Hashtable;
import java.util.List;
import javax.xml.bind.*;
import java.io.IOException;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;

import org.w3c.dom.Document;

import mappinglist.*;
import assertionlist.*;


public class ConfigXmlReader {
	private final static String analyzersPackage = "analysis.analyzers";
	private List mappings;
	private Hashtable assertions;
	
	private static ConfigXmlReader instance = new ConfigXmlReader();
	
	public static ConfigXmlReader getInstance() {
		return instance;
	}
	
	private ConfigXmlReader() {
		process();
	}
	
	private void process() {
		buildReqMappings();
		buildAssertionsDescr();
	}
	
	private void buildReqMappings() {
		try {
			JAXBContext jc = JAXBContext.newInstance("mappinglist");
			Unmarshaller u = jc.createUnmarshaller();
			Reqmappings rm = (Reqmappings) u.unmarshal(new FileInputStream(Config.getInstance().getConfigDir() + "\\" + Config.getInstance().getRequirementMappingsFileName()));
		    this.mappings = rm.getRequirementmapping();
		} catch (JAXBException je) {
            je.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
	}
	
	private void buildAssertionsDescr() {
		try {
			JAXBContext jc = JAXBContext.newInstance("assertionlist");
			Unmarshaller u = jc.createUnmarshaller();
			Assertionmappings am = (Assertionmappings) u.unmarshal(new FileInputStream(Config.getInstance().getConfigDir() + "\\" + Config.getInstance().getAnalyzerMappingsFileName()));
			this.assertions = new Hashtable();
			List tmplist = am.getAssertionmapping();
			for (int i = 0, max1 = tmplist.size(); i < max1; i++) {
				Assertiontype at = (Assertiontype) tmplist.get(i);
				try {
					Class clazz = Class.forName(ConfigXmlReader.analyzersPackage + "." + at.getFwclass());
					Constructor fwConstructor = clazz.getConstructor(new Class[]{Document.class, Hashtable.class, String.class}); 
					this.assertions.put(at.getName(), new AssertionDescriptor(at.getName(), at.getCeclass(), fwConstructor));
				} catch (ClassNotFoundException cnfe) {
					cnfe.printStackTrace();
				} catch (NoSuchMethodException mnfe) {
					mnfe.printStackTrace();
				}
			}
		} catch (JAXBException je) {
			je.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public Maptype getMappingByIndex(int index) {
		return (Maptype) this.mappings.get(index);
	}
	
	public int getMappingsSize() {
	    return this.mappings.size();	
	}
	
	public AssertionDescriptor getAsstnDescriptor(String name) {
		return (AssertionDescriptor) this.assertions.get(name);	
	}
}
