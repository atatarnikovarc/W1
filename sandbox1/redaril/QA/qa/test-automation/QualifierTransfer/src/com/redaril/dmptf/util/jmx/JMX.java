package com.redaril.dmptf.util.jmx;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;

public class JMX {

    private final static String CONFIG_PATH = "config" + File.separator; 
    private final static String LOG4J_PROPERTIES_FILE = "log4j.properties";
	private static final Logger LOG = Logger.getLogger(JMX.class);
	
	private static final int JMX_PORT = 9999;
    private static final int TIMETOWAIT = 10000;
    private static final int TRY_COUNT = 5;
	private String ENV;

	public JMX ( String env ) {

		File propertiesFile=new File(CONFIG_PATH, LOG4J_PROPERTIES_FILE);
	    PropertyConfigurator.configure(propertiesFile.toString());
		
		this.ENV = env;
		if ( !ENV.equals("env1") && !ENV.equals("env2") && !ENV.equals("env3")) {
			LOG.error("Wrong value of \"ENV\" = \"" +
					ENV + "\"; ENV = env1 | env2 | env3");
	    	System.exit(1);
		}
	}

	public JmxResponse getStatus ( String app ) throws Exception {
		JmxResponse response = new JmxResponse();
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

	public JmxResponse getStatus ( String app, String cluster ) throws Exception {
		JmxResponse response = new JmxResponse();
		if ( app.equals("partners") ) {
			response = jmxInvoke (getIP(app, cluster),
					"RedAril:type=AdservingDataLoaders,name=partners",
					"getStatus");
			response.setState(true);
		}
		return response;
	}
	
	public boolean reboot ( String app ) throws Exception {
        LOG.info("Start to reboot " + app);
		if ( app.equals("QualifierUpdaterJob") ) {
			return waitApp(getIP(app),
					"RedAril.DmpModel:group=QuartzTasks,class=QualifierUpdaterJobSingleton");
		
		} else if ( app.equals("qualifiersUpdater") ) {
			return waitApp(getIP(app),
					"com.redaril.qualifier:name=qualifiersUpdater," +
					"type=QualifierUpdater");
        } else if ( app.equals("CST") ) {
            return rebootApp(getIP("CST"),
                    "RedAril.cacheservertester:name=AdservingDataLoaders");
		} else if ( app.equals("taxonomyLoader") ) {
			return rebootApp(getIP(app),
					"RedAril.taxonomyloader:package=com.redaril.taxonomy,name=taxonomyLoader,type=TaxonomyLoader");
		} else if ( app.equals("partners") ) {
			LOG.error("Usage: reboot ( \"partners\", " +
					"\"cluster\" ); cluster = west | east;");
			return false;
		
		} else {
			return false;
		}
	}
	
	public boolean reboot ( String app, String cluster ) throws Exception {
        LOG.info("Start to reboot " + app);
        return app.equals("partners") && rebootApp(getIP(app, cluster), "RedAril.partners:name=AdservingDataLoaders");
	}
	
	private JmxResponse jmxInvoke ( String ip, String bean, String cmd ) throws Exception {
		//LOG.debug("Invoke command: " + cmd +" for bean: " + bean + " at ip: " + ip + ";");

		JMXServiceURL jmxUrl = new JMXServiceURL(
			"service:jmx:rmi:///jndi/rmi://" + ip + ":" + JMX_PORT + "/jmxrmi");
		JMXConnector connector = JMXConnectorFactory.connect(jmxUrl);
		MBeanServerConnection mbs = connector.getMBeanServerConnection();

		ObjectName obn = new ObjectName(bean);   
		if ( mbs.queryMBeans(obn ,null).size() > 1 ) {
			LOG.error("Have received more then 1 bean for " + obn);
			System.exit(1);
		}
		String [] signature = null;
		Object [] params = null;

		JmxResponse response = new JmxResponse();
		//System.out.println("DEBUG: response.pushMessage(mbs.invoke(obn, cmd, params, signature));");
		//System.out.println("DEBUG: jmxInvoke: before");
		Object res = mbs.invoke(obn, cmd, params, signature);
		//System.out.println("DEBUG: jmxInvoke: after");
		//System.out.println("DEBUG: jmxInvoke: res: " + res + ";");
		if (res != null ) {
			response.pushMessage(res);
		}
		//response.pushMessage(mbs.invoke(obn, cmd, params, signature));
		//LOG.debug("response: " + response.getMessage() + ";");
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
				} else if ( ENV.equals("env3") ) {
                    ip = "10.33.4.46";
                }
			} else if ( cluster.equals("east") ) {
				if ( ENV.equals("env1") ) {
					ip = "10.50.150.131";
				} else if ( ENV.equals("env2") ) {
					ip = "10.50.150.155";
				} else if ( ENV.equals("env3") ) {
                    ip = "10.33.4.46";
                }
			} else {
				LOG.error("Wrong value of \"cluster\" = \"" + 
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
			} else if ( ENV.equals("env3") ) {
                ip = "10.33.4.46";
            }
		} else if ( app.equals("qualifiersUpdater") ) {
			if ( ENV.equals("env1") ) {
				ip = "10.50.150.152";
			} else if ( ENV.equals("env2") ) {
				ip = "10.50.150.10";
			} else if ( ENV.equals("env3") ) {
                ip = "10.33.4.46";
            }
		} else if ( app.equals("QualifierUpdaterJob") ) {
            if (ENV.equals("env1")) {
                ip = "10.50.150.12";
            } else if (ENV.equals("env2")) {
                ip = "10.50.150.14";
            } else if (ENV.equals("env3")) {
                ip = "10.33.4.46";
            }
        } else if (app.equals("CST")) {
            if (ENV.equals("env1")) {
                ip = "10.50.150.130";
            } else if (ENV.equals("env2")) {
                ip = "10.50.150.154";
            } else if (ENV.equals("env3")) {
                ip = "10.33.4.46";
            }
        }
		return ip;
	}	
	
	private boolean waitApp ( String ip, String bean ) throws Exception {
		boolean waitResult;
		JmxResponse response;
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
        Thread.sleep(5000);
		return waitResult;
	}
	
	private boolean rebootApp ( String ip, String bean ) throws Exception {
		boolean rebootResult = false;
		boolean isJmxDone = false;
		int i = 0;
		
		/*
		 * STOP if not stopped
		 */

		JmxResponse response = new JmxResponse();
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
				LOG.error("Can`t execute JMX command: " +
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
			LOG.error("Can`t execute JMX command: " +
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
