import io.selendroid.client.SelendroidDriver;
import io.selendroid.common.SelendroidCapabilities;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Calendar;

/**
 * Created by atatarnikov on 14.06.17.
 */

/*
It is just straightforward implementation, no focus on error processing, logging, and other
page objects like approaches...
 */

public class RevoTest {

    private WebDriver driver = null;
    private String newBeneficiaryName = null, newBenLastName = "LN";

    private void getToAddBeneficiary(String destPerson) throws Exception {
        //it is assumed we've got initialised user account
        driver.findElement(By.id("header_next")).click();
        //bad practice - should use conditioning waiting instructions
        Thread.sleep(500);
        driver.findElement(By.id("uic_edit_phone_number")).sendKeys("662266");
        driver.findElement(By.id("signup_next")).click();
        Thread.sleep(500);
        for (int i = 0; i < 4; i++)
            driver.findElement(By.id("digit1")).click();
        Thread.sleep(4000);
        driver.findElement(By.id("uic_header_next")).click();
        Thread.sleep(3000);
        driver.findElement(By.id("button_transfer")).click();
        driver.findElement(By.linkText("To bank account")).click();
        Thread.sleep(3000);
        driver.findElement(By.id("header_next")).click();
        Thread.sleep(2000);
        driver.findElement(By.id("list_add_new_item_text")).click();
        Thread.sleep(1000);
        driver.findElement(By.linkText(destPerson)).click();
        driver.findElement((By.id("button_next"))).click();
        //we would parametrize country and currency as well in
        //production version for this test
        Thread.sleep(1000);
        driver.findElement((By.id("button_next"))).click();
    }

    private void addNewBeneficiary() throws Exception {
        driver.findElement(By.id("first_name")).clear();
        driver.findElement(By.id("first_name")).sendKeys(newBeneficiaryName);
        driver.findElement(By.id("last_name")).clear();
        driver.findElement(By.id("last_name")).sendKeys(newBenLastName);
        //IBAN + SWIFT - are parameters as well
        driver.findElement(By.id("server_field_0")).sendKeys("FR14 2004 1010 0505 0001 3M02 606");
        driver.findElement(By.id("server_field_1")).sendKeys("TSIGFR22");
        driver.findElement((By.id("button_next"))).click();
        Thread.sleep(2000);
        driver.findElement((By.id("button_next"))).click();
        Thread.sleep(5000);
        driver.findElement(By.id("operation_status_button")).click();
        Thread.sleep(3000);
    }

    private void checkNewBeneficiary() {
        Assert.assertTrue(driver.findElements(By.linkText(
                newBeneficiaryName + " " + newBenLastName + " ∙ GBP")).size() == 1);
    }

    @Before
    public void setup() throws Exception {
        SelendroidCapabilities capa = new SelendroidCapabilities("com.revolut.revolut.test:4.3.0");
        driver = new SelendroidDriver(capa);
        getToAddBeneficiary("To myself");
        newBeneficiaryName = "NB_" + Calendar.getInstance().getTimeInMillis();
    }

    @Test
    public void addBeneficiarToMePositive() throws Exception {
        addNewBeneficiary();
        checkNewBeneficiary();
    }

    @After
    public void teardown() {
        //nice to place to clear results of test(delete the beneficiary for an instance
        driver.quit();
    }
}
