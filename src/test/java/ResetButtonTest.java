import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.testng.asserts.Assertion;

import java.io.IOException;
import java.util.Random;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by dolkons on 09.02.17.
 */
public class ResetButtonTest {

    private ChromeDriver chromeDriver;
    private WebElement paymentAmount;
    private WebElement doPaymentButton;
    private WebElement balance;
    private WebElement increaseButton;
    private WebElement slider;
    private WebElement doResetButton;
    private String currentBalance;
    private Point oldSliderLocation;
    private Point maxLeftSliderLocation;

    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        Utils.runTestSliderService();
        chromeDriver = Utils.initializeChromeDriver();
        currentBalance = "0";
        slider = chromeDriver.findElement(By.id("slider-handle"));
        maxLeftSliderLocation = slider.getLocation();
    }

    @BeforeMethod
    public void initializePageElements(){
        paymentAmount = chromeDriver.findElement(By.id("amount"));
        doPaymentButton = chromeDriver.findElement(By.className("actions")).findElements(By.xpath("./*")).get(0);
        doResetButton = chromeDriver.findElement(By.className("actions")).findElements(By.xpath("./*")).get(1);
        balance = chromeDriver.findElement(By.id("balance-holder"));
        increaseButton = chromeDriver.findElement(By.className("increase")).findElement(By.className("icon"));
        oldSliderLocation = slider.getLocation();
        paymentAmount.clear();
    }

    @Test(dataProvider = "paymentData", dataProviderClass = DataProviders.class)
    public void resetButtonTest(String payment){
        paymentAmount.sendKeys(payment);
        doPaymentButton.click();
        String expectedBalance = String.valueOf(Long.parseLong(payment) + Long.parseLong(currentBalance));
        WebDriverWait balanceAppearWait = new WebDriverWait(chromeDriver, 10);
        balanceAppearWait.
                until(ExpectedConditions.textToBePresentInElement(balance, expectedBalance));
        assertEquals("Balance field value is invalid!", balance.getText(), expectedBalance+"\nруб.");

        for (int i=0; i < new Random().nextInt(10); i++){
            try{
                increaseButton.click();
            }
            catch (WebDriverException e) {
                JavascriptExecutor jse = (JavascriptExecutor) chromeDriver;
                jse.executeScript("document.getElementsByClassName('increase')[0].children[0].click()");
            }
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
            }
            catch (TimeoutException e){
                new Assertion().fail(
                        "Slider position didn't change after timeout!\nOld position: " +
                                oldSliderLocation+"\n" + "New position: " + slider.getLocation());
                e.printStackTrace();
            }
            //assertThat("Slider position didn't change!", oldSliderLocation, not(equalTo(slider.getLocation())));
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