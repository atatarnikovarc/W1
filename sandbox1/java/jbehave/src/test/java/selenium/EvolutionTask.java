package selenium;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by atatarnikov on 25.10.16.
 */
public class EvolutionTask {
    private WebDriver driver;
    private final static String startPage = "http://ss.lv";
    private List<String> selectedDeals;

    public EvolutionTask() {
        super();
    }

    @Before
    public void init() {
        //try catch for : timeout, webdriverexception, etc. - fail test if happen
        try {
            driver = new FirefoxDriver();

            //p.1 start an instance, maximize a window
            driver.navigate().to(startPage);
            driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
            driver.manage().window().maximize();
            Thread.sleep(500);

            //p.3 - change lang
            WebElement ruMenu = driver.findElement(By.className("menu_lang>a"));
            ruMenu.click();

            selectedDeals = new Vector<String>(10);
            //we should do it in a case of persistence, it is a pity - seems like for
            //the case of web driver - the site is not save it permanently
            resetBookmarks();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Before method fails");
        }
    }

    private void resetBookmarks() {
        driver.findElement(By.linkText("Закладки")).click();
        WebElement form = null;

        //we should replace it by driver.findElements() to speed up the check
        try {
            form = driver.findElement(By.name("filter_frm"));
        } catch (Exception e) {
            //say logger of it
        }

        if (form != null) {//is bookmarks non empty

            //select all bookmarks
            //go through all tables
            List<WebElement> dealsInputs = form.findElements(By.tagName("input"));

            for (WebElement e: dealsInputs) {
                e.click();
            }

            driver.findElement(By.linkText("Удалить выбранные из закладок")).click();
        }

        //go back to main page
        driver.findElement(By.cssSelector(".page_header_logo")).click();
    }

    private void selectDeal(int i, WebElement tab) {
        List<WebElement> deals = tab.findElements(By.tagName("tr"));
        int index = i < 2 ? 2 : i;
        WebElement currDeal = deals.get(index).findElement(By.tagName("input"));
        selectedDeals.add(currDeal.getAttribute("id"));
        currDeal.click();
        try {Thread.sleep(50); } catch (Exception e) {}
    }

    private boolean checkDeals() {
        boolean result = true;
        WebElement form = null;

        //kind of a duplication against resetBookmarks
        //we should replace it by driver.findElements() to speed up the check
        try {
            form = driver.findElement(By.name("filter_frm"));
        } catch (Exception e) {
            //say logger of it
        }

        if (form != null) {//is bookmarks non empty

            //select all bookmarks
            //go through all tables
            List<WebElement> dealsInputs = form.findElements(By.tagName("input"));

            for (WebElement e: dealsInputs) {
                final String id = e.getAttribute("id");
                if (!selectedDeals.contains(id)) {
                    //log failed ids + we would log all failed ids here with no break
                    result = false;
                    break;
                }
            }

        }

        return result;
    }

    @Test
    public void doEvoTest1() throws Exception {
        try {
            Thread.sleep(1000);//such instruction should be replaced by waiting by contitions
                                // constructions + setting up implicit driver's timeout
                                //for particular app and enviroment
                                //it is used for demo purposes first of all

            //p.4
            WebElement elTech = driver.findElement(By.linkText("Электротехника"));
            elTech.click();
            Thread.sleep(1000);
            WebElement poisk = driver.findElement(By.xpath("//./a[@title='Искать объявления']"));
            poisk.click();
            Thread.sleep(1000);
            WebElement searchForm = driver.findElement(By.name("ffrm"));

            //p.4, 7, 8
            searchForm.findElement(By.name("txt")).sendKeys("Компьютер");
            searchForm.findElement(By.name("topt[8][min]")).sendKeys("20");
            searchForm.findElement(By.name("topt[8][max]")).sendKeys("2000");
            new Select(searchForm.findElement(By.name("sid"))).selectByIndex(1);

            //p.5
            searchForm.findElement(By.id("sbtn")).click();
            Thread.sleep(1000);

            //p.6
            driver.findElement(By.linkText("Цена")).click();
            //we could wait here whatever element to appear - app specific

            //p.9 - generate random numbers, select corresponding deals
            WebElement resTable = driver.findElement(By.id("page_main"));
            List<WebElement> tables = resTable.findElements(By.tagName("table"));
            WebElement dealsTab = tables.get(1);

            Set<Integer> nums = new HashSet<Integer>(10);
            for (int i = 0; i < 7; i++) {
                nums.add(new Integer(ThreadLocalRandom.current().nextInt(0, 20)));
            }

            List<Integer> numsList = new Vector<Integer>(nums);
            for (int i = 0; i < 3; i++) {
                selectDeal(numsList.get(i).intValue(), dealsTab);
            }

            //kind of a stability sleeps
            try {Thread.sleep(500); } catch (Exception e) {}
            //p.10 add to favorites
            driver.findElement(By.linkText("Добавить выбранные в закладки")).click();
            driver.findElement(By.id("alert_ok")).click();

            try {Thread.sleep(500); } catch (Exception e) {}

            //p.11 - go to favorites and checks against selected
            driver.findElement(By.linkText("Закладки")).click();
            Assert.assertTrue("Deals selected are not equal to deals bookmarked", checkDeals());
        } catch (WebDriverException e) {  //nosuchelm, elementnotvisible, etc exceptions
            e.printStackTrace();
            Assert.fail("relevant error message");
        }
    }

    @After
    public void done() {
        //p.12
        driver.quit();
    }
}
