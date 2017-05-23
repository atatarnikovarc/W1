package com.redaril.utils.jmx;

import java.io.File;

import javax.management.ObjectName;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.redaril.qualifiers.validation.regular.RegularQualifiersValidation;

public class JMX {

	private final static String LOG4J_RESOURCE_PATH = "resources\\log4j\\";
    private final static String LOG4J_PROPERTIES_FILE = "log4j.properties";
	private static final Logger log = Logger.getLogger(RegularQualifiersValidation.class);
	
	private static final int JMX_PORT = 9999;
    private static final int TIMETOWAIT = 10000;
    private static final int TRY_COUNT = 5;
	private String ENV;

	public JMX ( String env ) {

		File propertiesFile=new File(LOG4J_RESOURCE_PATH, LOG4J_PROPERTIES_FILE);
	    PropertyConfigurator.configure(propertiesFile.toString());
		
		this.ENV = env;
		if ( !ENV.equals("env1") && !ENV.equals("env2") ) {
			log.error("Wrong value of \"ENV\" = \"" +
					ENV + "\"; ENV = env1 | env2");
	    	System.exit(1);
		}
	}

	public jmxResponse getStatus ( String app ) throws Exception {
		jmxResponse response = new jmxResponse();
		if ( app.equals("QualifierUpdaterJob") ) {
			response = jmxInvoke (getIP(app),
					"RedAril.QuartzTasks: class=QualifierUpdaterJobSingleton",
					"getStatus");
			response.setState(true);
			
		} else if ( app.equals("taxonomyLoader") ) {
			response = jmxInvoke (getIP(app),
				"com.redaril.qualifier:name=qualifiersUpdater,type=QualifierUpdater",
				"getStatus");
				response.setState(true);
				
		} else if ( app.equals("partners") ) {
			response.setMessage("ERROR: Usage: getStatus ( \"partners\", " +
					"\"cluster\" ); cluster = west | east;");
			response.setState(false);
		}
		return response;
	}

	public jmxResponse getStatus ( String app, String cluster ) throws Exception {
		jmxResponse response = new jmxResponse();
		if ( app.equals("partners") ) {
			response = jmxInvoke (getIP(app, cluster),
					"RedAril:type=AdservingDataLoaders,name=partners",
					"getStatus");
			response.setState(true);
		}
		return response;
	}
	
	public boolean reboot ( String app ) throws Exception {
		if ( app.equals("QualifierUpdaterJob") ) {
			//DmpModel bean. //UPDATE DMPDB
			//"RedAril.TaxonomyLoader: class=QualifierUpdaterJobSingleton",
			return waitApp(getIP(app),
					"RedAril.QuartzTasks: class=QualifierUpdaterJobSingleton");
		
		} else if ( app.equals("qualifiersUpdater") ) {
			//taxonomeLoader bean. //UPDATE QUALIFIERS
			//"com.redaril.qualifier:name=qualifiersUpdater,type=QualifierUpdater"
			return waitApp(getIP(app),
					"com.redaril.qualifier:name=qualifiersUpdater," +
					"type=QualifierUpdater");
		
		} else if ( app.equals("taxonomyLoader") ) {
			//taxonomeLoader bean. //REBOOT TAXONOMYLOADER
			//"com.redaril.taxonomy:name=taxonomyLoader,type=TaxonomyLoader"
			return rebootApp(getIP(app),
					"com.redaril.taxonomy:name=taxonomyLoader,type=TaxonomyLoader");
			
		} else if ( app.equals("partners") ) {
			log.error("Usage: reboot ( \"partners\", " +
					"\"cluster\" ); cluster = west | east;");
			return false;
		
		} else {
			return false;
		}
	}
	
	public boolean reboot ( String app, String cluster ) throws Exception {
		if ( app.equals("partners") ) {
			return rebootApp(getIP(app, cluster),
					"RedAril:type=AdservingDataLoaders,name=partners");
		} else {
			return false;
		}
	}
	
	private jmxResponse jmxInvoke ( String ip, String bean, String cmd ) throws Exception {
		if (log.isDebugEnabled()) log.debug("Invoke command: " + cmd +
				" for bean: " + bean + " at ip: " + ip + ";");

		JMXServiceURL jmxUrl = new JMXServiceURL(
			"service:jmx:rmi:///jndi/rmi://" + ip + ":" + JMX_PORT + "/jmxrmi");
		JMXConnector connector = JMXConnectorFactory.connect(jmxUrl);
		MBeanServerConnection mbs = connector.getMBeanServerConnection();

		ObjectName obn = new ObjectName(bean);   
		if ( mbs.queryMBeans(obn ,null).size() > 1 ) {
			log.error("Have received more then 1 bean for " + obn);
			System.exit(1);
		}
		String [] signature = null;
		Object [] params = null;

		jmxResponse response = new jmxResponse();
		//System.out.println("DEBUG: response.pushMessage(mbs.invoke(obn, cmd, params, signature));");
		//System.out.println("DEBUG: jmxInvoke: before");
		Object res = mbs.invoke(obn, cmd, params, signature);
		//System.out.println("DEBUG: jmxInvoke: after");
		//System.out.println("DEBUG: jmxInvoke: res: " + res + ";");
		if (res != null ) {
			response.pushMessage(res);
		}
		//response.pushMessage(mbs.invoke(obn, cmd, params, signature));
		if (log.isDebugEnabled()) log.debug("response: " + response.getMessage() + ";");
		return response;
	}
	
	private String getIP ( String app, String cluster ) {
		String ip = "";
		if ( app.equals("partners") ) {

			if ( cluster.equals("west") ) {
				if ( ENV.equals("env1") ) {
					ip = "10.50.150.152";
				} else if ( ENV.equals("env2") ) {
					ip = "10.50.150.10";
				}
			} else if ( cluster.equals("east") ) {
				if ( ENV.equals("env1") ) {
					ip = "10.50.150.131";
				} else if ( ENV.equals("env2") ) {
					ip = "10.50.150.155";
				}
			} else {
				log.error("Wrong value of \"cluster\" = \"" + 
						cluster + "\"; cluster = west | east;");
				System.exit(1);
			}
			
		}
		return ip;
	}

	private String getIP ( String app ) {
		String ip = "";
		if ( app.equals("taxonomyLoader") ) {
			if ( ENV.equals("env1") ) {
				ip = "10.50.150.152";
			} else if ( ENV.equals("env2") ) {
				ip = "10.50.150.10";
			}
		} else if ( app.equals("qualifiersUpdater") ) {
			if ( ENV.equals("env1") ) {
				ip = "10.50.150.152";
			} else if ( ENV.equals("env2") ) {
				ip = "10.50.150.10";
			}
		} else if ( app.equals("QualifierUpdaterJob") ) {
			if ( ENV.equals("env1") ) {
				ip = "10.50.150.12";
			} else if ( ENV.equals("env2") ) {
				ip = "10.50.150.14";
			}
		}
		return ip;
	}	
	
	private boolean waitApp ( String ip, String bean ) throws Exception {
		boolean waitResult = false;
		jmxResponse response = new jmxResponse();

		jmxInvoke (ip, bean, "reload");
		
		while ( true ) {
			response = jmxInvoke (ip, bean, "getStatus");
			if ( response.contains("Waiting") ) {
					waitResult = true;
					break;
			} else if ( response.contains("Finished with error: ") ) {
				waitResult = false;
				break;
			}
			response.clean();
			Thread.sleep(TIMETOWAIT);
		}
		response.clean();	
		return waitResult;
	}
	
	private boolean rebootApp ( String ip, String bean ) throws Exception {
		boolean rebootResult = false;
		boolean isJmxDone = false;
		int i = 0;
		
		/*
		 * STOP if not stopped
		 */
		jmxResponse response = new jmxResponse();
		response = jmxInvoke (ip, bean, "getStatus");
		if ( !response.contains("stopped") ) {
			response.clean();
			Thread.sleep(TIMETOWAIT);
			
			i = 0;
			isJmxDone = false;
			while (i < TRY_COUNT) { 
				//System.out.println(bean + " STOP");
				jmxInvoke (ip, bean, "doStop");
				Thread.sleep(TIMETOWAIT);
	
				response = jmxInvoke (ip, bean, "getStatus");
				if ( response.contains("stopped") ) {
					isJmxDone = true;
					break;
				}
				response.clean();
				Thread.sleep(TIMETOWAIT);
				i++;
			}
			if ( !isJmxDone ) {
				log.error("Can`t execute JMX command: " +
						bean + " STOP");
				System.exit(1);
			}
		}
		
		/*
		 * START
		 */
		i = 0;
		isJmxDone = false;
		while (i < TRY_COUNT) { 
			//System.out.println(bean + " START");
			jmxInvoke (ip, bean, "doStart");
			Thread.sleep(TIMETOWAIT);
	
			response = jmxInvoke (ip, bean, "getStatus");
			if ( response.contains("Reloading") || response.contains("sleeping") ) {
				isJmxDone = true;
				break;
			}
			response.clean();
			Thread.sleep(TIMETOWAIT);
			i++;
		}
		if ( !isJmxDone ) {
			log.error("Can`t execute JMX command: " +
					bean + " START");
			System.exit(1);
		}

		/*
		 * WAIT LOAD
		 */
		//System.out.println(bean + " GetStatus");
		while ( true ) {
			response = jmxInvoke (ip, bean, "getStatus");
			if ( response.contains("sleeping") ) {
				rebootResult = true;
				break;
			} else if ( response.contains("failed") ) {
				rebootResult = false;
				break;
			}
			response.clean();
			Thread.sleep(TIMETOWAIT);
		}
		response.clean();
		return rebootResult;
	}
}
