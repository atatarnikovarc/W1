package tests.corporate;

import java.util.Calendar;

import org.w3c.dom.Document;

import tests.RunApiTest;
import util.Config;
import util.data.OracleHelper;

public class CORP_ADMIN_PWD_GET extends RunApiTest {
	private String corp_admin_login;
	private int corp_id;
	private int corp_admin_id_1;
	private String corp_admin_1_login;

	public CORP_ADMIN_PWD_GET() {
		super("");
	}

	protected void setUp() throws Exception {
		super.setUp();
		corp_admin_login = "aaa" + Calendar.getInstance().getTimeInMillis();
		corp_id = OracleHelper.getInstance().corp_addCorporation(
				corp_admin_login);
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
					+ "OPERATION=CORP-ADMIN-PWD-GET&MSISDN="
					+ OracleHelper.getInstance().corp_getMSISDN_By_Login(
							corp_admin_1_login);

			// get response
			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);
			String data1 = getTag("DATA1", doc);

			// assert response code
			assertTrue("0 expected, but was: " + retCode, retCode.equals("0"));
			assertTrue(corp_admin_1_login + " expected, but was: " + data1,
					data1.equals(corp_admin_1_login));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}

	public void test2() {
		try {
			String url_1 = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?"
					+ "OPERATION=CORP-ADMIN-PWD-GET&MSISDN="
					+ "1234567890";
					
			String url_2 = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?"
					+ "OPERATION=CORP-ADMIN-PWD-GET&MSISDN="
					+ "12345678";


			// get response
			Document doc_1 = getResponseDoc(url_1);
			Document doc_2 = getResponseDoc(url_2);
			String retCode_1 = getTag("RETURN-CODE", doc_1);
			String retCode_2 = getTag("RETURN-CODE", doc_2);
			
			// assert response code
			assertTrue("-20028 expected, but was: " + retCode_1, retCode_1.equals("-20028"));
			assertTrue("-20028 expected, but was: " + retCode_2, retCode_2.equals("-20028"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}
	
	public void test3() {
		try {
			String url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?"
					+ "OPERATION=CORP-ADMIN-PWD-GET&MSISDN="
					+ OracleHelper.getInstance().corp_getMSISDN(9999995999l);

			// get response
			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);
			
			// assert response code
			assertTrue("-20023 expected, but was: " + retCode, retCode.equals("-20023"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}
}
