import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by dolkons on 10.02.17.
 */
public class RestartServiceTest {

    private ChromeDriver chromeDriver;
    private MainPageObject mainPageObject;

    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        Utils.runTestSliderService();
        chromeDriver = Utils.initializeChromeDriver();
        mainPageObject = new MainPageObject(chromeDriver);
    }

    @BeforeMethod
    public void preConditions(){
        mainPageObject.doPayment(String.valueOf("1400"));
        for (int i = 300; i < 300 + new Random().nextInt(1100); i = i + 50) {
            mainPageObject.clickOnIncreaseButton();
            WebDriverWait newCostChangedWait = new WebDriverWait(chromeDriver, 10);
            newCostChangedWait.
                    until(ExpectedConditions.
                            textToBePresentInElement(
                                    mainPageObject.getNewCost(), String.valueOf(i)));
        }
        mainPageObject.clickOnPurchaseButton();
        WebDriverWait wait = new WebDriverWait(chromeDriver, 5);
        wait.until(ExpectedConditions.textToBePresentInElement(mainPageObject.getCurrentCost(), mainPageObject.getNewCost().getText()));
    }

    @Test
    public void RefreshPageTest(){
        chromeDriver.navigate().refresh();
        mainPageObject.waitForPageLoaded(chromeDriver);

        assertEquals("Current time has not been reset!", mainPageObject.getCurrentTime().getText(), "0\nдней осталось");
        assertEquals("Current speed has not been reset!", mainPageObject.getCurrentSpeed().getText(), "64\nКбит/сек (макс.)");
        assertEquals("Current cost has not been reset!", mainPageObject.getCurrentCost().getText(), "0\nруб. в месяц");

        assertEquals("New time has not been reset!", mainPageObject.getNewTime().getText(), "30\nдней останется");
        assertEquals("New speed has not been reset!", mainPageObject.getNewSpeed().getText(), "64\nКбит/сек (макс.)");
        assertEquals("New cost has not been reset!", mainPageObject.getNewCost().getText(), "0\nруб. в месяц");

        assertEquals("Balance value is incorrect!",
                mainPageObject.getBalance().getText().split("\n")[0],
                String.valueOf(
                        Integer.parseInt(mainPageObject.getBalance().getText().split("\n")[0]) -
                                Integer.parseInt(mainPageObject.getNewCost().getText().split("\n")[0])));

        assertEquals("Slider position has not been reset!", mainPageObject.getSlider().getLocation(), mainPageObject.getMaxLeftSliderLocation());
    }

    @Test
    public void RestartServiceTest() throws IOException, InterruptedException {

        Utils.stopTestSliderService();
        Utils.runTestSliderService();
        chromeDriver.navigate().refresh();
        mainPageObject.waitForPageLoaded(chromeDriver);

        assertEquals("Current time has not been reset!", mainPageObject.getCurrentTime().getText(), "0\nдней осталось");
        assertEquals("Current speed has not been reset!", mainPageObject.getCurrentSpeed().getText(), "64\nКбит/сек (макс.)");
        assertEquals("Current cost has not been reset!", mainPageObject.getCurrentCost().getText(), "0\nруб. в месяц");

        assertEquals("New time has not been reset!", mainPageObject.getNewTime().getText(), "30\nдней останется");
        assertEquals("New speed has not been reset!", mainPageObject.getNewSpeed().getText(), "64\nКбит/сек (макс.)");
        assertEquals("New cost has not been reset!", mainPageObject.getNewCost().getText(), "0\nруб. в месяц");

        assertEquals("Balance value is incorrect!",
                mainPageObject.getBalance().getText().split("\n")[0],
                String.valueOf(
                        Integer.parseInt(mainPageObject.getBalance().getText().split("\n")[0]) -
                                Integer.parseInt(mainPageObject.getNewCost().getText().split("\n")[0])));
        assertEquals("Slider is not located on the max left side", mainPageObject.getSlider().getLocation(), mainPageObject.getMaxLeftSliderLocation());
    }

    @AfterMethod
    public void finish_test(ITestResult testResult) throws IOException {
        if (testResult.getStatus() == ITestResult.FAILURE){

            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss_dd.MM.yyyy");
            Calendar cal = Calendar.getInstance();

            File scrFile = chromeDriver.getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(System.getProperty("user.home")+
                    "/test_slider/screenshots/"+testResult.getName() +
                    "_" + dateFormat.format(cal.getTime())));
        }
    }

    @AfterClass
    public void finish() throws IOException, InterruptedException {
        if (chromeDriver != null) {
            chromeDriver.quit();
        }
        Utils.stopTestSliderService();
        Utils.deleteDatabase();
    }
}
