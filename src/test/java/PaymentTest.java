import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.asserts.Assertion;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by dolkons on 08.02.17.
 */
public class PaymentTest {

    private ChromeDriver chromeDriver;
    private MainPage mainPage;

    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        Utils.runTestSliderService();
        chromeDriver = Utils.initializeChromeDriver();
    }

    @Test(enabled = true, dataProvider = "paymentData", dataProviderClass = DataProviders.class)
    public void PaymentTestRandomString(String payment) throws InterruptedException {
        System.out.println("Starting PaymentTestRandomString...");
        mainPage.doPayment(payment);
        try{
            WebDriverWait wait = new WebDriverWait(chromeDriver, 1);
            wait.until(ExpectedConditions.textToBePresentInElement(mainPage.getBalance(), payment));
            assertEquals("Balance is not equal to zero!", mainPage.getBalance().getText(), "0\nруб.");
        }
        catch (TimeoutException e){
            assertEquals("Payment amount edit field is not equal to zero!", mainPage.getPaymentAmount().getAttribute("value"), "0");
        }
        Thread.sleep(100);
    }

    @Test(enabled = true, dataProvider = "paymentData", dataProviderClass = DataProviders.class)
    public void PaymentTestNegativeValue(String payment) throws InterruptedException {
        System.out.println("Starting PaymentTestNegativeValue...");
        mainPage.doPayment(payment);
        try{
            WebDriverWait wait = new WebDriverWait(chromeDriver, 1);
            wait.until(ExpectedConditions.textToBePresentInElement(mainPage.getBalance(), payment));
            assertEquals("Balance has a negative value!", mainPage.getBalance().getText(), "0\nруб.");
        }
        catch (TimeoutException e) {
            assertEquals("Payment amount edit field is not equal to zero!", mainPage.getPaymentAmount().getAttribute("value"), "0");
        }
        Thread.sleep(100);
    }

    @Test(enabled = true, dataProvider = "paymentData", dataProviderClass = DataProviders.class)
    public void PaymentTestCorrectValue(final String payment) throws InterruptedException {
        System.out.println("Starting PaymentTestCorrectValue...");
        mainPage.doPayment(payment);

        WebDriverWait balanceAppearWait = new WebDriverWait(chromeDriver, 10);
        try {
            balanceAppearWait.until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver webDriver) {
                    if (mainPage.getBalance().getText().equals(payment)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }
        catch (TimeoutException e){
            new Assertion().fail(
                    "Payment amount didn't appear in balance field!\nExpected payment amount: " +
                            payment+"\n" + "Actual payment amount: " + mainPage.getBalance().getText());
            e.printStackTrace();
        }
        Thread.sleep(100);
    }

    @AfterMethod
    public void reset(ITestResult testResult) throws IOException {
        if (testResult.getStatus() == ITestResult.FAILURE){

            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss_dd.MM.yyyy");
            Calendar cal = Calendar.getInstance();

            File scrFile = chromeDriver.getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(System.getProperty("user.home")+
                    "/test_slider/screenshots/"+testResult.getName() +
                    "_" + dateFormat.format(cal.getTime())));
        }
        mainPage.clickOnResetButton();
        if (!mainPage.getBalance().getText().equals("0\nруб.")){
            new WebDriverWait(chromeDriver, 5).
                    until(ExpectedConditions.textToBePresentInElement(mainPage.getBalance(),"0\nруб."));
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