import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.testng.asserts.Assertion;
import org.testng.reporters.jq.Main;

import java.io.IOException;
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
    private String currentBalance;
    private Point oldSliderLocation;
    private Point maxLeftSliderLocation;
    private MainPage mainPage;

    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        Utils.runTestSliderService();
        chromeDriver = Utils.initializeChromeDriver();
        mainPage = new MainPage(chromeDriver);
        currentBalance = "0";
        slider = chromeDriver.findElement(By.id("slider-handle"));
        maxLeftSliderLocation = slider.getLocation();
        newCost = chromeDriver.findElement(By.className("main-offer-container")).findElement(By.className("cost"));
        currentCost = chromeDriver.findElement(By.className("content-container")).findElement(By.className("cost"));
    }

    @BeforeMethod
    public void initializePageElements(){
        paymentAmount = chromeDriver.findElement(By.id("amount"));
        doPaymentButton = chromeDriver.findElement(By.className("actions")).findElements(By.xpath("./*")).get(0);
        doResetButton = chromeDriver.findElement(By.className("actions")).findElements(By.xpath("./*")).get(1);
        balance = chromeDriver.findElement(By.id("balance-holder"));
        increaseButton = chromeDriver.findElement(By.className("increase")).findElement(By.className("icon"));
        doPurchaseButton = chromeDriver.findElement(By.xpath("//*[@id=\"sliders\"]/div[2]/div[3]/div[2]/div/div/div/a"));
        currentSpeed = chromeDriver.findElement(By.className("main-offer-container")).findElement(By.className("speed"));
        oldSliderLocation = slider.getLocation();
        paymentAmount.clear();
    }

    @Test
    public void sliderResetTest(){
        for (int i = 1; i < 1 + new Random().nextInt(5); i++) {
            for (int j = 0; j < 1 + new Random().nextInt(numberOfSliderSteps); j++) {
                mainPage.clickOnIncreaseButton();
                WebDriverWait sliderPositionChangedWait = new WebDriverWait(chromeDriver, 10);
                try {
                    sliderPositionChangedWait.until(new ExpectedCondition<Boolean>() {
                        @Override
                        public Boolean apply(WebDriver webDriver) {
                            if (!slider.getLocation().equals(oldSliderLocation)) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });
                } catch (TimeoutException e) {
                    new Assertion().fail(
                            "Slider position didn't change after timeout!\nOld position: " +
                                    oldSliderLocation + "\n" + "New position: " + slider.getLocation());
                    e.printStackTrace();
                }
            }
            doResetButton.click();
            WebDriverWait sliderOnLeftWait = new WebDriverWait(chromeDriver, 10);
            try {
                sliderOnLeftWait.until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(WebDriver webDriver) {
                        if (slider.getLocation().equals(maxLeftSliderLocation)) {
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
                        "Actual position: " + slider.getLocation());
            }
        }
    }

    @Test
    public void resetConnectedServicesAndBalanceTest(){
        for (int i=1; i < 1 + new Random().nextInt(5); i++) {
            paymentAmount.sendKeys("1400");
            doPaymentButton.click();
            for (int j = 300; j < 300 + new Random().nextInt(1100); j = j + 50) {
                try {
                    increaseButton.click();
                } catch (WebDriverException e) {
                    JavascriptExecutor jse = (JavascriptExecutor) chromeDriver;
                    jse.executeScript("document.getElementsByClassName('increase')[0].children[0].click()");
                }
                WebDriverWait newCostChangedWait = new WebDriverWait(chromeDriver, 10);
                newCostChangedWait.
                        until(ExpectedConditions.
                                textToBePresentInElement(
                                        newCost, String.valueOf(j)));
            }
            doPurchaseButton.click();
            WebDriverWait currentCostTextAppearsWait = new WebDriverWait(chromeDriver, 10);
            currentCostTextAppearsWait.until(ExpectedConditions.textToBePresentInElement(currentCost, newCost.getText()));

            doResetButton.click();
            try {
                WebDriverWait currentTariffResetWait = new WebDriverWait(chromeDriver, 10);
                currentTariffResetWait.until(new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(WebDriver webDriver) {
                        if (currentCost.getText().equals("0\nруб. в месяц") &&
                                currentSpeed.getText().equals("64\nКбит/сек (макс.)")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            } catch (TimeoutException e) {
                new Assertion().fail("Current tariff has not been reset!\n" +
                        "Expected speed: 64\nКбит/сек (макс.)\n" +
                        "Current speed: " + currentSpeed.getText() + "\n" +
                        "Expected cost: 0\nруб. в месяц\n" +
                        "Current cost: " + currentCost.getText());
            }
        }
    }

    @AfterMethod
    public void updateCurrentBalance(){
        currentBalance = balance.getText().split("\n")[0];
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