package com.redaril.dmptf.util.network.appinterface.jmx;

import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.Assert.fail;


public class JMXWrapper {
    private String ip;
    private String bean;
    private String JMX_PORT;
    private static Logger LOG;
    private final static String FILE_PROPERTIES = "config" + File.separator + "jmx.properties";
    private ConfigurationLoader jmxConfig;
    private final static int loopCount = 120;

    public JMXWrapper(String env, String configID, String bean) {
        LOG = LoggerFactory.getLogger(JMXWrapper.class);
        ConfigurationLoader configurationLoader = new ConfigurationLoader("config" + File.separator + env + ".properties");
        this.bean = bean;
        this.ip = configurationLoader.getProperty(configID + "." + bean);
        jmxConfig = new ConfigurationLoader(FILE_PROPERTIES);
        JMX_PORT = jmxConfig.getProperty("JMX_PORT");
    }

    public String execCommand(String command) {
        JmxResponse response;
        Object res;
        JMXServiceURL jmxUrl;
        try {
            jmxUrl = new JMXServiceURL(
                    "service:jmx:rmi:///jndi/rmi://" + ip + ":" + JMX_PORT + "/jmxrmi");
            JMXConnector connector = JMXConnectorFactory.connect(jmxUrl);
            MBeanServerConnection mbs = connector.getMBeanServerConnection();
            ObjectName obn = new ObjectName(jmxConfig.getProperty(bean + "objectname"));
            String[] signature = null;
            Object[] params = null;
            response = new JmxResponse();
            res = mbs.invoke(obn, command, params, signature);
            response.pushMessage(res);
            LOG.info("Command = " + command + " was successfully executed at " + bean);
        } catch (MalformedURLException e) {
            LOG.error("Can't execute JMX operation. Exception = " + e.getLocalizedMessage());
            return null;
        } catch (IOException e) {
            LOG.error("Can't execute JMX operation. Exception = " + e.getLocalizedMessage());
            return null;
        } catch (MalformedObjectNameException e) {
            LOG.error("Can't execute JMX operation. Exception = " + e.getLocalizedMessage());
            return null;
        } catch (InstanceNotFoundException e) {
            LOG.error("Can't execute JMX operation. Exception = " + e.getLocalizedMessage());
            return null;
        } catch (MBeanException e) {
            LOG.error("Can't execute JMX operation. Exception = " + e.getLocalizedMessage());
            return null;
        } catch (ReflectionException e) {
            LOG.error("Can't execute JMX operation. Exception = " + e.getLocalizedMessage());
            return null;
        }
        if (res == null) {
            return null;
        } else return response.getMessage().get(0);
    }

    public void waitForReloading() {
        String command = "getStatus";
        JmxResponse response;
        Object res;
        JMXServiceURL jmxUrl;
        try {
            jmxUrl = new JMXServiceURL(
                    "service:jmx:rmi:///jndi/rmi://" + ip + ":" + JMX_PORT + "/jmxrmi");
            JMXConnector connector = JMXConnectorFactory.connect(jmxUrl);
            MBeanServerConnection mbs = connector.getMBeanServerConnection();
            ObjectName obn = new ObjectName(jmxConfig.getProperty(bean + "objectname"));
            String[] signature = null;
            Object[] params = null;
            response = new JmxResponse();
            Boolean isRestarted = false;
            Integer i = 0;
            while (!isRestarted && i < loopCount) {
                res = mbs.invoke(obn, command, params, signature);
                response.pushMessage(res);
                if (!res.toString().contains("Reload")) isRestarted = true;
                else {
                    i++;
                    LOG.debug("Waiting for reloading " + bean);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        LOG.info("Can't sleep thread");
                    }
                }
            }
            if (i >= loopCount) {
                LOG.error("Status of bean not changed from reloading");
                fail("Status of bean not changed from reloading");
            }
            LOG.info(bean + " successfully reloaded.");
        } catch (MalformedURLException e) {
            LOG.error("Can't execute JMX operation. Exception = " + e.getLocalizedMessage());

        } catch (IOException e) {
            LOG.error("Can't execute JMX operation. Exception = " + e.getLocalizedMessage());

        } catch (MalformedObjectNameException e) {
            LOG.error("Can't execute JMX operation. Exception = " + e.getLocalizedMessage());

        } catch (InstanceNotFoundException e) {
            LOG.error("Can't execute JMX operation. Exception = " + e.getLocalizedMessage());

        } catch (MBeanException e) {
            LOG.error("Can't execute JMX operation. Exception = " + e.getLocalizedMessage());

        } catch (ReflectionException e) {
            LOG.error("Can't execute JMX operation. Exception = " + e.getLocalizedMessage());

        }

    }
}
