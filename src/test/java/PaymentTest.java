import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.io.IOException;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by dolkons on 08.02.17.
 */
public class PaymentTest {

    private ChromeDriver chromeDriver;
    private WebElement paymentAmount;
    private WebElement doPaymentButton;
    private WebElement balance;
    private WebElement doResetButton;

    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        Utils.runTestSliderService();
        chromeDriver = Utils.initializeChromeDriver();
    }

    @BeforeMethod
    public void initializePageElements(){
        paymentAmount = chromeDriver.findElement(By.id("amount"));
        doPaymentButton = chromeDriver.findElement(By.className("actions")).findElements(By.xpath("./*")).get(0);
        balance = chromeDriver.findElement(By.id("balance-holder"));
        paymentAmount.clear();
    }

    @Test(enabled = true, dataProvider = "paymentData", dataProviderClass = DataProviders.class)
    public void PaymentTestRandomString(String payment) throws InterruptedException {
        System.out.println("Starting PaymentTestRandomString...");
        paymentAmount.sendKeys(payment);
        doPaymentButton.click();
        assertEquals("Payment amount edit field is not equal to zero!", paymentAmount.getAttribute("value"), "0");
        assertEquals("Balance is not equal to zero!", balance.getText(), "0\nруб.");
        Thread.sleep(100);
    }

    @Test(enabled = true, dataProvider = "paymentData", dataProviderClass = DataProviders.class)
    public void PaymentTestNegativeValue(String payment) throws InterruptedException {
        System.out.println("Starting PaymentTestNegativeValue...");
        paymentAmount.sendKeys(payment);
        doPaymentButton.click();
        try{
            WebDriverWait wait = new WebDriverWait(chromeDriver, 1);
            wait.until(ExpectedConditions.textToBePresentInElement(balance, payment));
            assertEquals("Balance has a negative value!", balance.getText(), "0\nруб.");
        }
        catch (TimeoutException e) {
            assertEquals("Payment amount edit field is not equal to zero!", paymentAmount.getAttribute("value"), "0");
        }
        Thread.sleep(100);
    }

    @Test(enabled = true, dataProvider = "paymentData", dataProviderClass = DataProviders.class)
    public void PaymentTestCorrectValue(String payment) throws InterruptedException {
        System.out.println("Starting PaymentTestCorrectValue...");
        paymentAmount.sendKeys(payment);
        doPaymentButton.click();
        WebDriverWait balanceAppearWait = new WebDriverWait(chromeDriver, 10);
        balanceAppearWait.until(ExpectedConditions.textToBePresentInElement(balance, payment));
        assertEquals("Balance field value is invalid!", balance.getText(), payment+"\nруб.");
        Thread.sleep(100);
    }

    @AfterMethod
    public void reset(){
        doResetButton = chromeDriver.findElement(By.className("actions")).findElements(By.xpath("./*")).get(1);
        doResetButton.click();
        if (!balance.getText().equals("0\nруб.")){
            new WebDriverWait(chromeDriver, 5).
                    until(ExpectedConditions.textToBePresentInElement(balance,"0\nруб."));
        }
    }

    @AfterClass(enabled = true, alwaysRun = true)
    public void finish() throws IOException, InterruptedException {
        if (chromeDriver != null) {
            chromeDriver.quit();
        }
        Utils.stopTestSliderService();
        Utils.deleteDatabase();
    }

}