package tests.corporate;

import java.util.Calendar;

import org.w3c.dom.Document;

import tests.RunApiTest;
import util.Config;
import util.data.OracleHelper;

public class CORP_ADMIN_PWD_CHANGE extends RunApiTest {
	private String corp_admin_login;
	private String password;
	private int corp_id;
	private int corp_admin_id_1;
	private String corp_admin_1_login;
	
	public CORP_ADMIN_PWD_CHANGE() {
		super("");
		password = new String("");
	}

	protected void setUp() throws Exception {
		super.setUp();
		corp_admin_login = "aaa" + Calendar.getInstance().getTimeInMillis();
		corp_id = OracleHelper.getInstance().corp_addCorporation(corp_admin_login);
		corp_admin_1_login = "aaa1" + Calendar.getInstance().getTimeInMillis();
		corp_admin_id_1 = OracleHelper.getInstance().corp_addRepresentative(
				corp_id, corp_admin_1_login);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		// move on all msisdns to 0 state
		OracleHelper.getInstance().corp_delCorporation(corp_id);
	}

	public void test1() {
		try {
			String url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?"
					+ "OPERATION=CORP-ADMIN-PWD-CHANGE&PASSWORD=" + password
					+ "&PARAM1=" + corp_id + "&" + "PARAM2=" + corp_admin_id_1
					+ "&PARAM3=1234567&PARAM4=1234567";

			// get response
			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);

			// assert response code
			assertTrue("0 expected, but was: " + retCode, retCode
					.equals("0"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}
	
	public void test2() {
		try {
			String url_1 = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?"
					+ "OPERATION=CORP-ADMIN-PWD-CHANGE&PASSWORD=" + password
					+ "&PARAM1=" + corp_id + "&" + "PARAM2=" + corp_admin_id_1
					+ "&PARAM3=1234&PARAM4=1234";
			
			String url_2 = Config.getInstance().getAppUrl()
			+ "//unica-wa//work.html?"
			+ "OPERATION=CORP-ADMIN-PWD-CHANGE&PASSWORD=" + password
			+ "&PARAM1=" + corp_id + "&" + "PARAM2=" + corp_admin_id_1
			+ "&PARAM3=12345678901&PARAM4=12345678901";

			// get response
			Document doc_1 = getResponseDoc(url_1);
			Document doc_2 = getResponseDoc(url_2);
			String retCode_1 = getTag("RETURN-CODE", doc_1);
			String retCode_2 = getTag("RETURN-CODE", doc_2);
			
			// assert response code
			assertTrue("-20072 expected, but was: " + retCode_1, retCode_1
					.equals("-20072"));
			assertTrue("-20072 expected, but was: " + retCode_2, retCode_2
					.equals("-20072"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}

	public void test3() {
		try {
			String url_1 = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?"
					+ "OPERATION=CORP-ADMIN-PWD-CHANGE&PASSWORD=" + password
					+ "&PARAM1=" + corp_id + "&" + "PARAM2=" + corp_admin_id_1
					+ "&PARAM3=1234;&PARAM4=1234;";
			
			String url_2 = Config.getInstance().getAppUrl()
			+ "//unica-wa//work.html?"
			+ "OPERATION=CORP-ADMIN-PWD-CHANGE&PASSWORD=" + password
			+ "&PARAM1=" + corp_id + "&" + "PARAM2=" + corp_admin_id_1
			+ "&PARAM3=12#34&PARAM4=12#34";
			
			String url_3 = Config.getInstance().getAppUrl()
			+ "//unica-wa//work.html?"
			+ "OPERATION=CORP-ADMIN-PWD-CHANGE&PASSWORD=" + password
			+ "&PARAM1=" + corp_id + "&" + "PARAM2=" + corp_admin_id_1
			+ "&PARAM3=>12#34&PARAM4=>12#34";

			// get response
			Document doc_1 = getResponseDoc(url_1);
			Document doc_2 = getResponseDoc(url_2);
			Document doc_3 = getResponseDoc(url_3);
			String retCode_1 = getTag("RETURN-CODE", doc_1);
			String retCode_2 = getTag("RETURN-CODE", doc_2);
			String retCode_3 = getTag("RETURN-CODE", doc_3);

			// assert response code
			assertTrue("-20072 expected, but was: " + retCode_1, retCode_1
					.equals("-20072"));
			assertTrue("-20072 expected, but was: " + retCode_2, retCode_2
					.equals("-20072"));
			assertTrue("-20072 expected, but was: " + retCode_3, retCode_3
					.equals("-20072"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}
	
	public void test4() {
		try {
			String url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?"
					+ "OPERATION=CORP-ADMIN-PWD-CHANGE&PASSWORD=asdf" 
					+ "&PARAM1=" + corp_id + "&" + "PARAM2=" + corp_admin_id_1
					+ "&PARAM3=1234567&PARAM4=1234567";

			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);

			// assert response code
			assertTrue("-20024 expected, but was: " + retCode, retCode
					.equals("-20024"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}
	
	public void test5() {
		try {
			String url_1 = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?"
					+ "OPERATION=CORP-ADMIN-PWD-CHANGE&PASSWORD=" + password
					+ "&PARAM1=" + corp_id + "&" + "PARAM2=" + corp_admin_id_1
					+ "&PARAM3=1234567&PARAM4=123456";
			
			String url_2 = Config.getInstance().getAppUrl()
			+ "//unica-wa//work.html?"
			+ "OPERATION=CORP-ADMIN-PWD-CHANGE&PASSWORD=" + password
			+ "&PARAM1=" + corp_id + "&" + "PARAM2=" + corp_admin_id_1
			+ "&PARAM3=12345&PARAM4=123456";
			
			// get response
			Document doc_1 = getResponseDoc(url_1);
			Document doc_2 = getResponseDoc(url_2);
			String retCode_1 = getTag("RETURN-CODE", doc_1);
			String retCode_2 = getTag("RETURN-CODE", doc_2);
						
			// assert response code
			assertTrue("-20073 expected, but was: " + retCode_1, retCode_1
					.equals("-20073"));
			assertTrue("-20073 expected, but was: " + retCode_2, retCode_2
					.equals("-20073"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}
	
	public void test6() {
		try {
			String url_1 = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?"
					+ "OPERATION=CORP-ADMIN-PWD-CHANGE&PASSWORD=" + password
					+ "&PARAM1=" + corp_id + "&" + "PARAM2=" + Calendar.getInstance().getTimeInMillis()
					+ "&PARAM3=1234567&PARAM4=1234567";
			
			String url_2 = Config.getInstance().getAppUrl()
			+ "//unica-wa//work.html?"
			+ "OPERATION=CORP-ADMIN-PWD-CHANGE&PASSWORD=" + password
			+ "&PARAM1=" + Calendar.getInstance().getTimeInMillis() + "&" + "PARAM2=" + corp_admin_id_1
			+ "&PARAM3=123456&PARAM4=123456";
			
			// get response
			Document doc_1 = getResponseDoc(url_1);
			Document doc_2 = getResponseDoc(url_2);
			String retCode_1 = getTag("RETURN-CODE", doc_1);
			String retCode_2 = getTag("RETURN-CODE", doc_2);
			
			// assert response code
			assertTrue("-20023 expected, but was: " + retCode_1, retCode_1
					.equals("-20023"));
			assertTrue("-20023 expected, but was: " + retCode_2, retCode_2
					.equals("-20023"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}
}
