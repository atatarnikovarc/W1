package util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;

import org.xml.sax.InputSource;
import org.w3c.dom.Document;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class CommonHelper {
	private static CommonHelper instance = new CommonHelper();

	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private Connection corpDbConnection;
	private WebConversation webConversation;

	public static CommonHelper getInstance() {
		return instance;
	}

	private CommonHelper() {
		setUpXML();
		setUpOracle();
		setUpHttpUnit();
	}

	private void setUpXML() {
		try {
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private void setUpOracle() {
		try {
			String driver = "oracle.jdbc.driver.OracleDriver";
			Class.forName(driver);
			corpDbConnection = DriverManager.getConnection(Config.getInstance()
					.getCorpDbConnectionString(), Config.getInstance()
					.getCorpDbLogin(), Config.getInstance().getCorpDbPwd());
    	} catch (Exception e) {
			System.err.println(e);
		}

	}

	private void setUpHttpUnit() {
		webConversation = new WebConversation();
	}

	public Document getDocument(String text) throws Exception {
		return builder.parse(new InputSource(new StringReader(text)));
	}

	public Connection getCorpDbConnection() {
		return corpDbConnection;
	}
	
	public void closeCorpDBConnection() {
		try {
		corpDbConnection.close();
		} catch (Exception e) {
			System.err.println("can't close corpDBConnection: " + e);
		}
	}

	public WebResponse getWebGetResponse(String url) {
		try {
			WebRequest request = new GetMethodWebRequest(url);
			return webConversation.getResponse(request);
		} catch (Exception e) {
			System.err.println("can't get GET Response : " + e);
			return null;
		}
	}
	
	public static String getXmlTagValue(String xml, String tagName) {
		return null;
	}
}
