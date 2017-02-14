package tests.corporate;

import java.util.Calendar;

import org.w3c.dom.Document;

import tests.RunApiTest;
import util.Config;
import util.CommonHelper;
import util.data.OracleHelper;

public class COMMON_AUTH extends RunApiTest {
	private String corp_admin_login;
	private String password;
	private String param1;
	private int corp_id;
	private int corp_admin_id_1;
	private String corp_admin_1_login;

	public COMMON_AUTH() {
		super("");
		password = new String("");
		param1 = new String("1");
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
			String change_pwd_url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?"
					+ "OPERATION=CORP-ADMIN-PWD-CHANGE&PASSWORD=" + password
					+ "&PARAM1=" + corp_id + "&" + "PARAM2=" + corp_admin_id_1
					+ "&PARAM3=1234567&PARAM4=1234567";

			String url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?OPERATION=COMMON-AUTH&LOGIN="
					+ corp_admin_1_login + "&PASSWORD=" + "1234567"
					+ "&PARAM1=" + param1;

			CommonHelper.getInstance().getWebGetResponse(change_pwd_url);

			// get response
			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);
			String data1 = getTag("DATA1", doc);
			String data2 = getTag("DATA2", doc);
			String data3 = getTag("DATA3", doc);
			String data4 = getTag("DATA4", doc);

			// assert response code
			assertTrue("000 expected, but was: " + retCode, retCode
					.equals("000"));
			assertTrue("100 expected, but was: " + data1, data1
					.equals("100"));
			assertTrue(corp_id + " expected, but was: " + data2, data2
					.equals(Integer.toString(corp_id)));
			assertTrue(corp_admin_id_1 + " expected, but was: " + data3, data3
					.equals(Integer.toString(corp_admin_id_1)));
			String corp_admin_1_msisdn = OracleHelper.getInstance().corp_getMSISDN_By_Login(corp_admin_1_login);
			assertTrue(corp_admin_1_msisdn + " expected, but was: " + data4, data4
					.equals(corp_admin_1_msisdn));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}

	public void test2() {
		try {
			String change_pwd_url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?"
					+ "OPERATION=CORP-ADMIN-PWD-CHANGE&PASSWORD=" + password
					+ "&PARAM1=" + corp_id + "&" + "PARAM2=" + corp_admin_id_1
					+ "&PARAM3=1234567&PARAM4=1234567";

			String url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?OPERATION=COMMON-AUTH&LOGIN="
					+ corp_admin_1_login + "&PASSWORD=" + password + "&PARAM1="
					+ param1;

			CommonHelper.getInstance().getWebGetResponse(change_pwd_url);

			// get response
			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);

			// assert response code
			assertTrue("101 expected, but was: " + retCode, retCode
					.equals("101"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}

	public void test3() {
		try {
			String url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?OPERATION=COMMON-AUTH&LOGIN="
					+ "corp_admin_1_login_129298" + "&PASSWORD=" + password
					+ "&PARAM1=" + param1;

			// get response
			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);

			// assert response code
			assertTrue("101 expected, but was: " + retCode, retCode
					.equals("101"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}

	public void test4() {
		try {
			String url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?OPERATION=COMMON-AUTH&LOGIN="
					+ corp_admin_1_login + "&PASSWORD=" + password + "&PARAM1="
					+ param1;

			// get response
			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);

			// assert response code
			assertTrue("102 expected, but was: " + retCode, retCode
					.equals("102"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}

	public void test7() {
		try {
			String change_pwd_url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?"
					+ "OPERATION=CORP-ADMIN-PWD-CHANGE&PASSWORD=" + password
					+ "&PARAM1=" + corp_id + "&" + "PARAM2=" + corp_admin_id_1
					+ "&PARAM3=1234567&PARAM4=1234567";

			String url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?OPERATION=COMMON-AUTH&LOGIN="
					+ corp_admin_1_login + "&PASSWORD=" + "1234567"
					+ "&PARAM1=" + param1;

			CommonHelper.getInstance().getWebGetResponse(change_pwd_url);

			OracleHelper.getInstance().corp_blockCorporation(corp_id);

			// get response
			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);

			// assert response code
			assertTrue("-20031 expected, but was: " + retCode, retCode
					.equals("-20031"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}
}