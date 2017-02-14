package http;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.*;
import org.apache.commons.logging.*;
import org.apache.commons.logging.impl.*;
import java.util.Calendar;


public class TestRun1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		GetMethod method = new GetMethod("https://bach.develop.office.stacksoft.ru/onyma/");
		
		method.setRequestHeader("User Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; ru; rv:1.8.1.5) Gecko/20070713 Firefox/2.0.0.5");
		method.setRequestHeader("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		method.setRequestHeader("Accept-Language", "ru-ru,ru;q=0.8,en-us;q=0.5,en;q=0.3");
		method.setRequestHeader("Accept-Encoding", "gzip,deflate");
		method.setRequestHeader("Accept-Charset", "windows-1251,utf-8;q=0.7,*;q=0.7");
		method.setRequestHeader("Keep-Alive", "300");
		method.setRequestHeader("Connection", "keep-alive");
		
		HttpClient client = new HttpClient();
		//System.out.println("begin date : " + Calendar.getInstance().getTimeInMillis());
		long beginTime = Calendar.getInstance().getTimeInMillis();
		int code = client.executeMethod(method);
		
		System.out.println("1-st request ============= ");
		System.out.println("code : " + code);
		
		//request output
		Header[] headers = method.getRequestHeaders();
		for (int i = 0; i < headers.length; i++)
			System.out.println(headers[i].getName() + " : " + headers[i].getValue());
		
		//response output
		System.out.println("Response headers : ");
		Header[] responseHeaders = method.getResponseHeaders();
		for (int i = 0; i < responseHeaders.length; i++)
			System.out.println(responseHeaders[i].getName() + " : " + responseHeaders[i].getValue());
		System.out.println(" response body : \n" + method.getResponseBodyAsString());
		
		//System.out.println("some info : " + client.getState().getCookies().length);
		//release recources
		method.releaseConnection();		
		
		PostMethod postMethod  = new PostMethod("https://bach.develop.office.stacksoft.ru/onyma/login.htms");
		postMethod.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; ru; rv:1.8.1.5) Gecko/20070713 Firefox/2.0.0.5");
		postMethod.addRequestHeader("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		postMethod.addRequestHeader("Accept-Language", "ru-ru,ru;q=0.8,en-us;q=0.5,en;q=0.3");
		postMethod.addRequestHeader("Accept-Encoding", "gzip,deflate");
		postMethod.addRequestHeader("Accept-Charset", "windows-1251,utf-8;q=0.7,*;q=0.7");
		postMethod.addRequestHeader("Keep-alive", "300");
		postMethod.addRequestHeader("Connection", "keep-alive");
		postMethod.addRequestHeader("Referer", "https://bach.develop.office.stacksoft.ru/onyma/");
		postMethod.addRequestHeader("Content-type", "application/x-www-form-urlencoded");
		postMethod.addRequestHeader("Content-Length", "45");
		
		postMethod.addParameter("LOGIN", "atatarnikov");
		postMethod.addParameter("PASSWD", "qwerty13");
		postMethod.addParameter("enter", "Enter");
		
		code = client.executeMethod(postMethod);
		
		
		System.out.println("2-st request ============= ");
		System.out.println("post method response code : " + code);
		
		responseHeaders = postMethod.getResponseHeaders();
		System.out.println("postMethod's Response headers : ");
		for (int i = 0; i < responseHeaders.length; i++)
			System.out.println(responseHeaders[i].getName() + " : " + responseHeaders[i].getValue());
		System.out.println(" response body : \n" + postMethod.getResponseBodyAsString());
		
		System.out.println("cookie info after post request : \n");
		System.out.println("cookie's array length : " + client.getState().getCookies().length);
		
		GetMethod inMethod = new GetMethod("https://bach.develop.office.stacksoft.ru/onyma/main/main_menu.htms");
		inMethod.setRequestHeader("User Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; ru; rv:1.8.1.5) Gecko/20070713 Firefox/2.0.0.5");
		inMethod.setRequestHeader("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		inMethod.setRequestHeader("Accept-Language", "ru-ru,ru;q=0.8,en-us;q=0.5,en;q=0.3");
		inMethod.setRequestHeader("Accept-Encoding", "gzip,deflate");
		inMethod.setRequestHeader("Accept-Charset", "windows-1251,utf-8;q=0.7,*;q=0.7");
		inMethod.setRequestHeader("Keep-Alive", "300");
		inMethod.setRequestHeader("Connection", "keep-alive");
		inMethod.setRequestHeader("Referer", "https://bach.develop.office.stacksoft.ru/onyma/");
		//inMethod.setRequestHeader("Cookie", "ONYMA_LOGIN=atatarnikov1; PHPSESSID=3229b245ea46ce054e98dfe4254ee1fa; ONYMA_OPERID=2703; ONYMA_SKEY=995015_9HV9XX7E3VPB2o85b5644Amj; ONYMA_SUSR=ow_develop");
		
		code = client.executeMethod(inMethod);
		
		System.out.println("3-rd request ============= ");
		System.out.println("inMethod response code : " + code);
		
		responseHeaders = inMethod.getResponseHeaders();
		System.out.println("inMethod's Response headers : ");
		for (int i = 0; i < responseHeaders.length; i++)
			System.out.println(responseHeaders[i].getName() + " : " + responseHeaders[i].getValue());
		System.out.println(" response body : \n" + inMethod.getResponseBodyAsString());
		
		inMethod.releaseConnection();
		//PostMethod postMethod = new PostMethod("");
		long endTime = Calendar.getInstance().getTimeInMillis();
		double total = (endTime - beginTime) / 1000;
		System.out.println("total time in sec : " + total);
	}

}
