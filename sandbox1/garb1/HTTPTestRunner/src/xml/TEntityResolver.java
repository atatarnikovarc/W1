package xml;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class TEntityResolver implements EntityResolver {
	
	public TEntityResolver() {}

	public InputSource resolveEntity(String publicId, String systemId) {
		System.out.println(publicId + " --- " + systemId);
		return null;
	}
}
