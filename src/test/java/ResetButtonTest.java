import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.asserts.Assertion;
import org.testng.reporters.jq.Main;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by dolkons on 09.02.17.
 */
public class ResetButtonTest {

    private final int numberOfSliderSteps = 23;
    private ChromeDriver chromeDriver;
    private Point oldSliderLocation;
    private Point maxLeftSliderLocation;
    private MainPage mainPage;

    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        Utils.runTestSliderService();
        chromeDriver = Utils.initializeChromeDriver();
        mainPage = new MainPage(chromeDriver);
        maxLeftSliderLocation = mainPage.getMaxLeftSliderLocation();
    }

    @Test
    public void sliderResetTest(){
        for (int i = 1; i < 1 + new Random().nextInt(5); i++) {
            for (int j = 0; j < 1 + new Random().nextInt(numberOfSliderSteps); j++) {
                oldSliderLocation = mainPage.getSlider().getLocation();
                mainPage.clickOnIncreaseButton();
                WebDriverWait sliderPositionChangedWait = new WebDriverWait(chromeDriver, 10);
                try {
                    sliderPositionChangedWait.until(new ExpectedCondition<Boolean>() {
                        @Override
                        public Boolean apply(WebDriver webDriver) {
                            if (!mainPage.getSlider().getLocation().equals(oldSliderLocation)) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });
                } catch (TimeoutException e) {
                    new Assertion().fail(
                            "Slider position didn't change after timeout!\nOld position: " +
                                    oldSliderLocation + "\n" + "New position: " + mainPage.getSlider().getLocation());
                    e.printStackTrace();
                }
            }
            mainPage.clickOnResetButton();
            WebDriverWait sliderOnLeftWait = new WebDriverWait(chromeDriver, 10);
            try {
                sliderOnLeftWait.until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(WebDriver webDriver) {
                        if (mainPage.getSlider().getLocation().equals(maxLeftSliderLocation)) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            }
            catch (TimeoutException e){
                new Assertion().fail("Slider position didn't change to the max left after timeout!\n" +
                        "Expected position: " + maxLeftSliderLocation + "\n" +
                        "Actual position: " + mainPage.getSlider().getLocation());
            }
        }
    }

    @Test
    public void resetConnectedServicesTest(){
        for (int i=1; i < 1 + new Random().nextInt(5); i++) {
            mainPage.doPayment("1400");
            for (int j = 300; j < 300 + new Random().nextInt(1100); j = j + 50) {
                mainPage.clickOnIncreaseButton();
                WebDriverWait newCostChangedWait = new WebDriverWait(chromeDriver, 10);
                newCostChangedWait.
                        until(ExpectedConditions.
                                textToBePresentInElement(
                                        mainPage.getNewCost(), String.valueOf(j)));
            }
            mainPage.clickOnPurchaseButton();
            WebDriverWait currentCostTextAppearsWait = new WebDriverWait(chromeDriver, 10);
            currentCostTextAppearsWait.until(ExpectedConditions.textToBePresentInElement(mainPage.getCurrentCost(), mainPage.getNewCost().getText()));

            mainPage.clickOnResetButton();
            try {
                WebDriverWait currentTariffResetWait = new WebDriverWait(chromeDriver, 10);
                currentTariffResetWait.until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(WebDriver webDriver) {
                        if (mainPage.getCurrentCost().getText().equals("0\nруб. в месяц") &&
                                mainPage.getCurrentSpeed().getText().equals("64\nКбит/сек (макс.)")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            } catch (TimeoutException e) {
                new Assertion().fail("Current tariff has not been reset!\n" +
                        "Expected speed: 64\nКбит/сек (макс.)\n" +
                        "Current speed: " + mainPage.getCurrentSpeed().getText() + "\n" +
                        "Expected cost: 0\nруб. в месяц\n" +
                        "Current cost: " + mainPage.getCurrentCost().getText());
            }
        }
    }

    @Test
    public void resetBalanceTest(){
        for (int i=0; i < 1 + new Random().nextInt(5); i++){
            mainPage.doPayment("1400");
            mainPage.clickOnResetButton();
            try {
                WebDriverWait balanceResetWait = new WebDriverWait(chromeDriver, 10);
                balanceResetWait.until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(WebDriver webDriver) {
                        if (mainPage.getBalance().getText().equals("0\nруб.")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            } catch (TimeoutException e) {
                new Assertion().fail("Balance didn't reset after timeout!\n " +
                        "Expected: 0\nруб. \n" +
                        "Actual balance: " + mainPage.getBalance().getText());
            }
        }
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

    @AfterClass(alwaysRun = true)
    public void finish() throws IOException, InterruptedException {
        if (chromeDriver != null) {
            chromeDriver.quit();
        }
        Utils.stopTestSliderService();
        Utils.deleteDatabase();
    }
}