package selenium;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: atatarnikov
 * Date: 9/25/14
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */

@RunWith(Parameterized.class)
public class VeeamTask {
    private WebDriver driver;
    private static String startPage = "https://login.veeam.com/signin";

    //parms
    private String email;
    private String password;

    public VeeamTask(String email, String password) {
        super();
        this.email = email;
        this.password = password;
    }

    @Before
    public void init() {
        driver = new FirefoxDriver();
        driver.get(startPage);
    }


    //it is assumed that we use anothre test class for negative cases or different data driven parameters
    //mechanism, fore sure, we would extend parameters set with long strings, unicode encoded and so on
    @Parameterized.Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{{"correctname", "correctpwd"}, {"correctname", "incorrectpwd"}, {"", ""},
                {"incorrectname", "incorrectpwd"}, {":;", ",&?"}});
    }

    @Test
    public void doLogin() throws Exception {
        WebElement emailElm = driver.findElement(By.id("email"));
        WebElement passwordElm = driver.findElement(By.id("password"));

        emailElm.sendKeys(email);
        passwordElm.sendKeys(password);

        passwordElm.submit();

        Assert.assertTrue("It is not right message for correct login", driver.getTitle().equals("title for right login"));
    }

    @After
    public void done() {
        driver.quit();
    }
}
