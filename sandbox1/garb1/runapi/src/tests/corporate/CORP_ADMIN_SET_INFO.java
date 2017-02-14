package tests.corporate;

import java.util.Calendar;

import org.w3c.dom.Document;

import tests.RunApiTest;
import util.Config;
import util.data.OracleHelper;

public class CORP_ADMIN_SET_INFO extends RunApiTest {
	private String corp_admin_login;
	private int corp_id;
	private int corp_admin_id_1;
	private String corp_admin_1_login;

	public CORP_ADMIN_SET_INFO() {
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
					+ "//unica-wa//work.html?OPERATION="
					+ "CORP-ADMIN-SET-INFO&PARAM1=" + corp_id + "&PARAM2="
					+ corp_admin_id_1 + "&PARAM3="
					+ ("aaa" + Calendar.getInstance().getTimeInMillis())
					+ "&PARAM4=e@ee.com&PARAM5=Í-1&" + "MSISDN="
					+ OracleHelper.getInstance().corp_getMSISDN(78189992233l);

			// get response
			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);

			// assert response code
			assertTrue("0 expected, but was: " + retCode, retCode.equals("0"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}

	public void test2() {
		try {
			String url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?OPERATION="
					+ "CORP-ADMIN-SET-INFO&PARAM1=" + corp_id + "&PARAM2="
					+ corp_admin_id_1 + "&PARAM3="
					+ "&PARAM4=e@ee.com&PARAM5=Í-1&" + "MSISDN="
					+ OracleHelper.getInstance().corp_getMSISDN(78189992233l);

			// get response
			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);

			// assert response code
			assertTrue("-20025 expected, but was: " + retCode, retCode
					.equals("-20025"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}

	public void test3() {
		try {
			String url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?OPERATION="
					+ "CORP-ADMIN-SET-INFO&PARAM1=" + corp_id + "&PARAM2="
					+ corp_admin_id_1 + "&PARAM3="
					+ ("aaa" + Calendar.getInstance().getTimeInMillis())
					+ "&PARAM4=e@ee.com&PARAM5=&" + "MSISDN="
					+ OracleHelper.getInstance().corp_getMSISDN(78189992233l);

			// get response
			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);

			// assert response code
			assertTrue("-20026 expected, but was: " + retCode, retCode
					.equals("-20026"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}

	public void test4() {
		try {
			String url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?OPERATION="
					+ "CORP-ADMIN-SET-INFO&PARAM1=" + corp_id + "&PARAM2="
					+ corp_admin_id_1 + "&PARAM3="
					+ ("aaa" + Calendar.getInstance().getTimeInMillis())
					+ "&PARAM4=@ee.com&PARAM5=Í-1&" + "MSISDN="
					+ OracleHelper.getInstance().corp_getMSISDN(78189992233l);

			// get response
			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);

			// assert response code
			assertTrue("-20027 expected, but was: " + retCode, retCode
					.equals("-20027"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}

	public void test5() {
		try {
			String url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?OPERATION="
					+ "CORP-ADMIN-SET-INFO&PARAM1=" + corp_id + "&PARAM2="
					+ corp_admin_id_1 + "&PARAM3="
					+ ("aaa" + Calendar.getInstance().getTimeInMillis())
					+ "&PARAM4=@ee.com&PARAM5=Í-1&" + "MSISDN=000";

			// get response
			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);

			// assert response code
			assertTrue("-20028 expected, but was: " + retCode, retCode
					.equals("-20028"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}

	public void test6() {
		try {
			String url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?OPERATION="
					+ "CORP-ADMIN-SET-INFO&PARAM1=" + corp_id + "&PARAM2="
					+ corp_admin_id_1 + "&PARAM3="
					+ OracleHelper.getInstance().corp_getExistingLogin()
					+ "&PARAM4=e@ee.com&PARAM5=Í-1&" + "MSISDN="
					+ OracleHelper.getInstance().corp_getMSISDN(78189992233l);

			// get response
			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);

			// assert response code
			assertTrue("-20029 expected, but was: " + retCode, retCode
					.equals("-20029"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}

	public void test7() {
		try {
			String url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?OPERATION="
					+ "CORP-ADMIN-SET-INFO&PARAM1=" + corp_id + "&PARAM2="
					+ corp_admin_id_1 + "&PARAM3="
					+ ("aaa" + Calendar.getInstance().getTimeInMillis())
					+ "&PARAM4=e@ee.com&PARAM5=Í-1&" + "MSISDN="
					+ OracleHelper.getInstance().corp_getExistingMSISDN();

			// get response
			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);

			// assert response code
			assertTrue("-20030 expected, but was: " + retCode, retCode
					.equals("-20030"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}

	public void test8() {
		try {
			String url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?OPERATION="
					+ "CORP-ADMIN-SET-INFO&PARAM1="
					+ Calendar.getInstance().getTimeInMillis() + "&PARAM2="
					+ corp_admin_id_1 + "&PARAM3="
					+ ("aaa" + Calendar.getInstance().getTimeInMillis())
					+ "&PARAM4=e@ee.com&PARAM5=Í-1&" + "MSISDN="
					+ OracleHelper.getInstance().corp_getMSISDN(78189992233l);

			// get response
			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);

			// assert response code
			assertTrue("-20023 expected, but was: " + retCode, retCode
					.equals("-20023"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}

	public void test9() {
		try {
			String url = Config.getInstance().getAppUrl()
					+ "//unica-wa//work.html?OPERATION="
					+ "CORP-ADMIN-SET-INFO&PARAM1=" + corp_id + "&PARAM2="
					+ Calendar.getInstance().getTimeInMillis() + "&PARAM3="
					+ ("aaa" + Calendar.getInstance().getTimeInMillis())
					+ "&PARAM4=e@ee.com&PARAM5=Í-1&" + "MSISDN="
					+ OracleHelper.getInstance().corp_getMSISDN(78189992233l);

			// get response
			Document doc = getResponseDoc(url);
			String retCode = getTag("RETURN-CODE", doc);

			// assert response code
			assertTrue("-20023 expected, but was: " + retCode, retCode
					.equals("-20023"));
		} catch (Exception e) {
			System.err.println("Exception: " + e);
			fail("test error");
		}
	}
}
