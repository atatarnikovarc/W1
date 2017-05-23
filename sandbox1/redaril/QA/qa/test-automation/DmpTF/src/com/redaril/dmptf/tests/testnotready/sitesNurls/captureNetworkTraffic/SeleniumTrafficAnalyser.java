package com.redaril.dmptf.tests.testnotready.sitesNurls.captureNetworkTraffic;
//package com.redaril.dmptf.tests.testnotready.captureNetworkTraffic;
//
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.lang.reflect.Type;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Iterator;
//
//import org.slf4j.Logger;
//import org.apache.log4j.PropertyConfigurator;
//import org.junit.Test;
//import org.openqa.selenium.server.SeleniumServer;
//import com.google.gson.Gson;
//import com.google.gson.reflect.*;
//import com.thoughtworks.selenium.DefaultSelenium;
//
//public class SeleniumTrafficAnalyser {
//	private final static Logger LOG = LoggerFactory.getLogger(SeleniumTrafficAnalyser.class);
//
//	@Test
//	public void trafficAnalyser() throws Exception{
//		File propertiesFile=new File("log4j.properties");
//	    PropertyConfigurator.configure(propertiesFile.toString());
//
//		// Start the Selenium Server
//		SeleniumServer srvr = new SeleniumServer();
//		srvr.start();
//
////		File siteListFile = new File("list.txt");
////		if (siteListFile.exists()) {
////			BufferedReader bufferedReader = null;
////			bufferedReader = new BufferedReader(new FileReader("list.txt"));
////			String site = "";
////			
////			DefaultSelenium selenium;
////			
////			while ( (site = bufferedReader.readLine()) != null ) {
//		//String site = "http://www.greenwichcitizen.com/";
//		//String site = "http://mysa.com/";
//		String site = "http://www.wptz.com";
//
//		DefaultSelenium selenium = new DefaultSelenium("localhost", 4444, "*firefox", site);
//				selenium.start("captureNetworkTraffic=true");
//				selenium.setTimeout("60000");
//				selenium.windowMaximize();
//		
//				selenium.open("/");
//				
//				// dump the traffic into a variable in Json format
//				String trafficOutput = selenium.captureNetworkTraffic("json");
//				//LOG.debug(trafficOutput);
//
//				// parse the json using Gson
//				Gson gson = new Gson();
//				Type collectionOfHTMLRequestsType =
//					new TypeToken<Collection<HTMLRequestFromSelenium>>(){}.getType();
//				Collection<HTMLRequestFromSelenium> seleniumRequests =
//					gson.fromJson(trafficOutput, collectionOfHTMLRequestsType);
//
//				// get ready to analyse the traffic
//				TrafficAnalyser ta = new TrafficAnalyser(seleniumRequests);
//
//				// copied from Corey's python example
//				int num_requests = ta.get_num_requests();
//				int total_size = ta.get_content_size();
//				HashMap<Integer,Integer> status_map = ta.get_http_status_codes();
//				HashMap<String, Object[]> file_extension_map = ta.get_file_extension_stats(); 
//
//				     LOG.debug("\n\n--------------------------------");
//				     LOG.debug(String.format("results for %s",site));
//				     LOG.debug(String.format("content size: %d kb",total_size));
//				     LOG.debug(String.format("http requests: %d",num_requests));
//
//				     Iterator<Integer> statusIterator = status_map.keySet().iterator() ;
//				     while ( statusIterator.hasNext (  )  ) {
//				    	 int key = statusIterator.next();
//				    	 LOG.debug(String.format("status %d: %d", key, status_map.get(key)));
//				     }
//
//				     LOG.debug("\nfile extensions: (count, size)");
//				     Iterator<String> extensionIterator = file_extension_map.keySet().iterator() ;
//				     while ( extensionIterator.hasNext (  )  ) {
//				    	 String key = extensionIterator.next();
//				    	 LOG.debug(String.format("%s: %d, %f", key,
//						 file_extension_map.get(key)[0],file_extension_map.get(key)[1]));
//				     }
//
//					LOG.info("\nSite: " + site);    
//					LOG.debug("\nhttp timing detail: (status, method, url, size(bytes), time(ms))");
//					int raasnetCount= 0;
//					for (Iterator iterator = seleniumRequests.iterator(); iterator.hasNext();) {
//							HTMLRequestFromSelenium hr = (HTMLRequestFromSelenium) iterator.next();
//							//totalContentSize += hr.bytes;
//							if (hr.url.contains("raasnet.com")) {
//								raasnetCount++;
//								LOG.debug(String.format("%d, %s, %s, %d, %d",
//										hr.statusCode, hr.method, hr.url, hr.bytes, hr.timeInMillis));
//							}
//					}
//					if (raasnetCount == 0) {
//						LOG.error("No one *.raasnet.com request was sended!");
//					}
//				
//					selenium.close();
//					selenium.stop();
//			//}
//		//}
//
//
//
//		// close everything down
//		srvr.stop();
//	}
//
//}
