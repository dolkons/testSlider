import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;

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
    private MainPage mainPage;

    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        Utils.runTestSliderService();
        chromeDriver = Utils.initializeChromeDriver();
        mainPage = new MainPage(chromeDriver);
    }

    @Test
    public void refreshPageTest(){
        mainPage.doPayment(String.valueOf("1400"));
        for (int i = 300; i < 300 + new Random().nextInt(1100); i = i + 50) {
            mainPage.clickOnIncreaseButton();
            WebDriverWait newCostChangedWait = new WebDriverWait(chromeDriver, 10);
            newCostChangedWait.
                    until(ExpectedConditions.
                            textToBePresentInElement(
                                    mainPage.getNewCost(), String.valueOf(i)));
        }
        mainPage.clickOnPurchaseButton();
        WebDriverWait wait = new WebDriverWait(chromeDriver, 5);
        wait.until(ExpectedConditions.textToBePresentInElement(mainPage.getCurrentCost(), mainPage.getNewCost().getText()));
        chromeDriver.navigate().refresh();

        assertEquals("Current time has not been reset!", mainPage.getCurrentTime().getText(), "0\nдней осталось");
        assertEquals("Current speed has not been reset!", mainPage.getCurrentSpeed().getText(), "64\nКбит/сек (макс.)");
        assertEquals("Current cost has not been reset!", mainPage.getCurrentCost().getText(), "0\nруб. в месяц");

        assertEquals("New time has not been reset!", mainPage.getNewTime().getText(), "30\nдней останется");
        assertEquals("New speed has not been reset!", mainPage.getNewSpeed().getText(), "64\nКбит/сек (макс.)");
        assertEquals("New cost has not been reset!", mainPage.getNewCost().getText(), "0\nруб. в месяц");

        assertEquals("Balance value is incorrect!",
                mainPage.getBalance().getText().split("\n")[0],
                String.valueOf(
                        Integer.parseInt(mainPage.getBalance().getText().split("\n")[0]) -
                                Integer.parseInt(mainPage.getNewCost().getText().split("\n")[0])));
    }

    @Test
    public void restartServiceTest() throws IOException, InterruptedException {
        mainPage.doPayment(String.valueOf("1400"));
        for (int i = 300; i < 300 + new Random().nextInt(1100); i = i + 50) {
            mainPage.clickOnIncreaseButton();
            WebDriverWait newCostChangedWait = new WebDriverWait(chromeDriver, 10);
            newCostChangedWait.
                    until(ExpectedConditions.
                            textToBePresentInElement(
                                    mainPage.getNewCost(), String.valueOf(i)));
        }
        mainPage.clickOnPurchaseButton();
        WebDriverWait wait = new WebDriverWait(chromeDriver, 5);
        wait.until(ExpectedConditions.textToBePresentInElement(mainPage.getCurrentCost(), mainPage.getNewCost().getText()));

        Utils.stopTestSliderService();
        Utils.runTestSliderService();
        chromeDriver.navigate().refresh();

        assertEquals("Current time has not been reset!", mainPage.getCurrentTime().getText(), "0\nдней осталось");
        assertEquals("Current speed has not been reset!", mainPage.getCurrentSpeed().getText(), "64\nКбит/сек (макс.)");
        assertEquals("Current cost has not been reset!", mainPage.getCurrentCost().getText(), "0\nруб. в месяц");

        assertEquals("New time has not been reset!", mainPage.getNewTime().getText(), "30\nдней останется");
        assertEquals("New speed has not been reset!", mainPage.getNewSpeed().getText(), "64\nКбит/сек (макс.)");
        assertEquals("New cost has not been reset!", mainPage.getNewCost().getText(), "0\nруб. в месяц");

        assertEquals("Balance value is incorrect!",
                mainPage.getBalance().getText().split("\n")[0],
                String.valueOf(
                        Integer.parseInt(mainPage.getBalance().getText().split("\n")[0]) -
                                Integer.parseInt(mainPage.getNewCost().getText().split("\n")[0])));
        assertEquals("Slider is not located on the max left side", mainPage.getSlider().getLocation(), mainPage.getMaxLeftSliderLocation());
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
