package jbehave;

import com.thoughtworks.selenium.Selenium;
import junit.framework.TestCase;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Created with IntelliJ IDEA.
 * User: atatarnikov
 * Date: 10/19/14
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainRun {
    public static void main(String[] args) {
        System.out.println(AppConfig.getInstance().getProp1());
        new TestC();
    }

    static class TestC {
        public Object getWindowHandles() {
            WebDriver drv = new FirefoxDriver();
            drv.quit();
            return null;
        }
    }
}
