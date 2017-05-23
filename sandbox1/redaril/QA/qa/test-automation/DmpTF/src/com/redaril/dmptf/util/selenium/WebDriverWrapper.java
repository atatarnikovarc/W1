package com.redaril.dmptf.util.selenium;

import com.redaril.dmptf.util.configuration.ConfigurationLoader;
import com.redaril.dmptf.util.file.FileHelper;
import com.redaril.dmptf.util.network.lib.httpunit.HttpUnitWrapper;
import com.redaril.dmptf.util.network.protocol.ssh.SSHWrapper;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.fail;

public class WebDriverWrapper {
    private static Logger LOG;
    private String ENV;
    private static List<Object[]> drivers = new ArrayList<Object[]>();
    private final static String PATH_CONFIG = "config" + File.separator;
    private final static String FILE_PROPERTIES = "selenium.properties";
    private final static String PATH_HOSTS = "data" + File.separator + "hosts" + File.separator;
    private static String pathToSeleniumClient;
    private static String hostHub;
    private static String seleniumHubUrl;
    private static SSHWrapper sshHUB;
    private static HttpUnitWrapper httpUnitWrapper;
    private static String requestStop;
    private final static String tempDirs = "data" + File.separator + "selenium" + File.separator + "tempDirs.txt";
    private static RemoteWebDriver currentDriver;
    public ElementFinder elementFinder;
    private String domain;
    private static String hosts;
    //  private final static String privateKeyFile = PATH_CONFIG + "privateKeyToVirtualMachines";

    public WebDriverWrapper(String ENV) {
        this.ENV = ENV;
        LOG = LoggerFactory.getLogger(WebDriverWrapper.class);
        httpUnitWrapper = new HttpUnitWrapper();
        ConfigurationLoader config = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES);
        requestStop = config.getProperty("commandClientStop");
        hostHub = config.getProperty("hostHub");
        seleniumHubUrl = config.getProperty("seleniumHubUrl");
        pathToSeleniumClient = config.getProperty("pathToSeleniumClient");
        domain = new ConfigurationLoader(PATH_CONFIG + "app.properties").getProperty("baseDomain");
        elementFinder = new ElementFinder();
        startHub();
        hosts = PATH_HOSTS + ENV + "hosts.txt";
    }

    private void startHub() {
        ConfigurationLoader config = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES);
        stopHub();
        sshHUB = new SSHWrapper(hostHub, "autotest", "812redaril");
        String runHub = config.getProperty("commandHubStart");
        sshHUB.executeCommand(runHub,null);
        wait(3);
    }

    private static void stopHub() {
        //stop HUB
        ConfigurationLoader config = new ConfigurationLoader(PATH_CONFIG + FILE_PROPERTIES);
        String stopHub = config.getProperty("commandHubStop");
        sshHUB = new SSHWrapper(hostHub, "autotest", "812redaril");
        sshHUB.executeCommand(stopHub,null);
        wait(3);
    }

    private static void killProcess(String ip, String process) {
        SSHWrapper sshWrapper = new SSHWrapper(ip, "root", "812redaril");
        LOG.debug("command to kill the process = " + "taskkill /im " + process + " /f");
        if (process.equals("chrome.exe")) {
            LOG.debug("taskkill /im chromedriver.exe /f");
            sshWrapper.executeCommand("taskkill /im chromedriver.exe /f",null);
        } else {
            sshWrapper.executeCommand("taskkill /im " + process + " /f",null);
        }
    }

    private static void removeTempDirs(String ip) {
        SSHWrapper sshWrapper = new SSHWrapper(ip, "root", "812redaril");
        LOG.debug("Remove Temp directories at machine " + ip);
        List<String> list = FileHelper.getInstance().getDataFromFile(tempDirs);
        for (String dir : list) {
            sshWrapper.executeCommand("rm -rf /cygdrive/c/" + dir,null);
        }
    }

    private static void stopBrowsers() {
        LOG.debug("Close all SeleniumClients and browsers");
        for (Object[] adriver : drivers) {
            RemoteWebDriver driver = (RemoteWebDriver) adriver[0];
            String path = (String) adriver[1];
            //stop driver
            try {
                driver.quit();
            } catch (WebDriverException e) {
                LOG.debug("Driver is unavailable. Exception = " + e.getLocalizedMessage());
            }

            //command to stop SeleniumClient over HTTP
            LOG.debug("try to stop seleniumClient. Go to url = " + "http://" + path + requestStop);
            httpUnitWrapper.goToUrl("http://" + path + requestStop);
            //stop existed browsers
            String browser = driver.getCapabilities().getBrowserName();
            String process;
            if (browser.contains("explorer")) process = "IEXPLORE.exe";
            else process = browser + ".exe";
            String ip = path.substring(0, path.indexOf(":"));
            killProcess(ip, process);
            removeTempDirs(ip);
        }
        drivers.clear();
    }

    private void checkRemoteMachine(String browser, String ip) {
        LOG.debug("Check remote machine at " + ip);
        SSHWrapper sshWrapper = new SSHWrapper(ip, "root", "812redaril");
        sshWrapper.executeCommand("date",null);
        String process;
        killProcess(ip, "notepad.exe");
        killProcess(ip, "notepad++.exe");
        killProcess(ip, "java.exe");
        killProcess(ip, "IEDriverServer.exe");
        killProcess(ip, "chromedriver.exe");
        if (browser.contains("explorer")) process = "IEXPLORE.exe";
        else process = browser + ".exe";
        killProcess(ip, process);
    }

    public static void tearDown() {
        //stop all existed SeleniumClients
        stopBrowsers();
        stopHub();
    }

    public void getDriver(HashMap<String, String> driverInfo, @Nullable Proxy proxy, @Nullable String fileName) {
        if (fileName != null) hosts = fileName;
        String os = driverInfo.get("OS");
        String version = driverInfo.get("version");
        String browser = driverInfo.get("browser");
        String ip = driverInfo.get("ip");
        String requestStart = driverInfo.get("requestStart");
        RemoteWebDriver webdriver = null;
        boolean isCreated = false;
        //try to find driver in list of existed
        if (drivers.size() > 0)
            for (Object[] driverA : drivers) {
                RemoteWebDriver driver = (RemoteWebDriver) driverA[0];
                Capabilities capabilities = driver.getCapabilities();
                String extBrowser = capabilities.getBrowserName();
                String extOS = capabilities.getPlatform().name();
                String extVersion = capabilities.getVersion();
                if (extOS.contains(os) & extVersion.contains(version) & extBrowser.contains(browser)) {
                    currentDriver = driver;
                    elementFinder.setDriver(currentDriver);
                    isCreated = true;
                    break;
                }
            }
        //we couldn't find existed driver, so we create new
        if (!isCreated) {
            checkRemoteMachine(browser, ip);
            removeTempDirs(ip);
            DesiredCapabilities capability = new DesiredCapabilities();
            capability.setBrowserName(driverInfo.get("browser"));
            capability.setVersion(driverInfo.get("version"));
            capability.setJavascriptEnabled(true);
            capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            //add proxy
            if (proxy != null) capability.setCapability(CapabilityType.PROXY, proxy);
            if (os.contains("WINDOWS")) capability.setPlatform(Platform.WINDOWS);
            if (os.contains("MAC")) capability.setPlatform(Platform.MAC);
            if (os.contains("XP")) capability.setPlatform(Platform.XP);
            if (os.contains("VISTA")) capability.setPlatform(Platform.VISTA);
            if (os.contains("UNIX")) capability.setPlatform(Platform.UNIX);
            if (os.contains("LINUX")) capability.setPlatform(Platform.LINUX);

            //end
            //create driver for it
            int i = 0;
            String port = "";
            SSHWrapper sshWrapper = new SSHWrapper(ip, "root", "812redaril");
            LOG.debug("Add file Hosts into remote machine. ENV = " + ENV);
            sshWrapper.putFile(hosts, "hosts", "/cygdrive/c/WINDOWS/system32/drivers/etc/");
            while (!isCreated && i < 10) {
                isCreated = false;
                //generate new port to start SeleniumClient
                port = String.valueOf(getPort());
                //run SeleniumClient at remote machine
                LOG.debug("Run Selenium client at remote machine. IP = " + ip);
                sshWrapper.executeCommand("cd " + pathToSeleniumClient + " && " + requestStart + " " + port,null);
                LOG.debug("Try to create driver. Parameters: OS = " + capability.getPlatform() + ", BROWSER = " + capability.getBrowserName() + ", VERSION = " + capability.getVersion() + ", PORT = " + port);
                try {
                    wait(10);
                    webdriver = new RemoteWebDriver(new URL(seleniumHubUrl), capability);
                    webdriver.setLogLevel(java.util.logging.Level.SEVERE);
                    LOG.debug("Driver was created successfully. Parameters: OS = " + webdriver.getCapabilities().getPlatform() + ", BROWSER = " + webdriver.getCapabilities().getBrowserName() + ", VERSION = " + webdriver.getCapabilities().getVersion() + ", PORT = " + port);
                    isCreated = true;
                } catch (Exception e) {
                    //we try to kill process, because sometimes seleniumClient starts and browser starts, but remoteWebDriver return exception
                    //so we should kill created processes ar rem
                    LOG.debug("Can't create webdriver. Exception = " + e.getLocalizedMessage());
                    LOG.debug("try to stop seleniumClient. Go to url = " + "http://" + ip + ":" + port + requestStop);
                    checkRemoteMachine(driverInfo.get("browser"), driverInfo.get("ip"));
                    startHub();
                    wait(5);
                }
                i++;
            }
            if (!isCreated) fail("Can't create webdriver. See extended log.");
            //add driver into list of existed
            Object[] newElement = new Object[8];
            newElement[0] = webdriver;
            newElement[1] = ip + ":" + port;
            newElement[2] = os;
            newElement[3] = version;
            newElement[4] = browser;
            newElement[5] = ip;
            newElement[6] = requestStart;
            newElement[7] = proxy;
            drivers.add(newElement);
            currentDriver = webdriver;
            elementFinder.setDriver(currentDriver);
        }
    }

    private int getPort() {
        int range = 9999;
        Random r = new Random();
        return r.nextInt(range);
    }

    private static void wait(int sec) {
        try {
            Thread.sleep(1000 * sec);
        } catch (InterruptedException e) {
            LOG.debug("Can't sleep the thread");
        }
    }

    private HashMap<String, String> getDriverInfoByDriver(RemoteWebDriver driver) {
        HashMap<String, String> driverInfo = new HashMap<String, String>();
        for (Object[] arr : drivers) {
            if (arr[0].equals(driver)) {
                driverInfo.put("OS", arr[2].toString());
                driverInfo.put("version", arr[3].toString());
                driverInfo.put("browser", arr[4].toString());
                driverInfo.put("ip", arr[5].toString());
                driverInfo.put("requestStart", arr[6].toString());
            }
        }
        return driverInfo;
    }

    private Proxy getProxyByDriver(RemoteWebDriver driver) {
        for (Object[] arr : drivers) {
            if (arr[0].equals(driver)) {
                return (Proxy) arr[7];
            }
        }
        return null;
    }

    public RemoteWebDriver getCurrentDriver() {
        return currentDriver;
    }

    public void restartDriver() {
        LOG.debug("Try to restart Selenium Webdriver");
        HashMap<String, String> driverInfo = getDriverInfoByDriver(getCurrentDriver());
        Proxy proxy = getProxyByDriver(getCurrentDriver());
        stopBrowsers();
        drivers.clear();
        wait(2);
//            proxyWrapper.stopProxyServer();
//            proxyWrapper = new ProxyWrapper(ENV);
        this.getDriver(driverInfo, proxy, null);
    }

    public void getPage(String url) {
        try {
            currentDriver.get(url);
        } catch (WebDriverException e) {
            LOG.debug("Browser get exception = " + e.getLocalizedMessage());
            restartDriver();
            try {
                currentDriver.get(url);
            } catch (Exception e2) {
                LOG.debug("Exception = " + e2.getLocalizedMessage());
            }
        }
    }

    public void deleteAllCookies() {
        try {
            getCurrentDriver().manage().deleteAllCookies();
        } catch (WebDriverException e) {
            LOG.debug("Browser get exception = " + e.getLocalizedMessage());
            restartDriver();
            getCurrentDriver().manage().deleteAllCookies();
        }
    }

    public String getCookieByName(String name) {
        try {
            if (name.equalsIgnoreCase("o") || name.equalsIgnoreCase("u")) {
                if (!getCurrentDriver().getCurrentUrl().contains(domain)) {
                    getCurrentDriver().get("http://example." + domain);
                }
            }
            Set<Cookie> set = getCurrentDriver().manage().getCookies();
            if (set == null) {
                LOG.error("Can't find any cookie.");
                fail("Can't find any cookie.");
                return "";

            }
            Cookie cookie = currentDriver.manage().getCookieNamed(name);
            if (cookie == null) {
                LOG.info("Can't find cookie with name = " + name);
                return null;
            } else return cookie.getValue();
        } catch (WebDriverException e) {
            LOG.debug("Browser get exception = " + e.getLocalizedMessage());
            restartDriver();
            Cookie cookie = currentDriver.manage().getCookieNamed(name);
            if (cookie == null) {
                LOG.info("Can't find cookie with name = " + name);
                return null;
            } else return cookie.getValue();
        }
    }

    public void addCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        try {
            currentDriver.manage().addCookie(cookie);
        } catch (WebDriverException e) {
            LOG.debug("Browser get exception = " + e.getLocalizedMessage());
            restartDriver();
            currentDriver.manage().addCookie(cookie);
        }
    }

    public String getCurrentUrl() {
        try {
            return currentDriver.getCurrentUrl();
        } catch (WebDriverException e) {
            LOG.debug("Browser get exception = " + e.getLocalizedMessage());
            restartDriver();
            return currentDriver.getCurrentUrl();
        }
    }

    public String getPageSource() {
        try {
            return currentDriver.getPageSource();
        } catch (WebDriverException e) {
            LOG.debug("Browser get exception = " + e.getLocalizedMessage());
            restartDriver();
            return currentDriver.getPageSource();
        }
    }

    public void clickButton(String button) {
        WebElement element = elementFinder.waitElement(By.cssSelector(button));
        if (element == null) fail("Can't find element = " + button);
        else element.click();
    }
}
