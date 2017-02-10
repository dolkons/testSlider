import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by dolkons on 09.02.17.
 */
public class ConnectionServicesTest {

    private ChromeDriver chromeDriver;
    private WebElement paymentAmount;
    private WebElement doPaymentButton;
    private WebElement balance;
    private WebElement increaseButton;
    private WebElement decreaseButton;
    private WebElement newCost;
    private WebElement currentCost;
    private WebElement doPurchaseButton;
    private WebElement doResetButton;
    private WebElement currentSpeed;
    private WebElement newSpeed;

    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        Utils.runTestSliderService();
        chromeDriver = Utils.initializeChromeDriver();
        paymentAmount = chromeDriver.findElement(By.id("amount"));
        doPaymentButton = chromeDriver.findElement(By.className("actions")).findElements(By.xpath("./*")).get(0);
        balance = chromeDriver.findElement(By.id("balance-holder"));
        increaseButton = chromeDriver.findElement(By.className("increase")).findElement(By.className("icon"));
        decreaseButton = chromeDriver.findElement(By.className("decrease")).findElement(By.className("icon"));
        newCost = chromeDriver.findElement(By.className("main-offer-container")).findElement(By.className("cost"));
        currentCost = chromeDriver.findElement(By.className("content-container")).findElement(By.className("cost"));
        doPurchaseButton = chromeDriver.findElement(By.xpath("//*[@id=\"sliders\"]/div[2]/div[3]/div[2]/div/div/div/a"));
        doResetButton = chromeDriver.findElement(By.className("actions")).findElements(By.xpath("./*")).get(1);
        currentSpeed = chromeDriver.findElement(By.className("content-container")).findElement(By.className("speed"));
        newSpeed = chromeDriver.findElement(By.className("main-offer-container")).findElement(By.className("speed"));
    }

    @Test(dataProvider = "paymentData", dataProviderClass = DataProviders.class)
    public void PurchaseButtonStateTest(String payment){
        paymentAmount.sendKeys(payment);
        doPaymentButton.click();
        WebDriverWait balanceAppearWait = new WebDriverWait(chromeDriver, 10);
        balanceAppearWait.
                until(ExpectedConditions.textToBePresentInElement(balance, payment));

        for (int i=300; i < 300 + (50 * new Random().nextInt(22)); i = i + 50){
            try{
                increaseButton.click();
            }
            catch (WebDriverException e) {
                JavascriptExecutor jse = (JavascriptExecutor) chromeDriver;
                jse.executeScript("document.getElementsByClassName('increase')[0].children[0].click()");
            }
            WebDriverWait newCostChangedWait = new WebDriverWait(chromeDriver, 10);
            newCostChangedWait.
                    until(ExpectedConditions.
                            textToBePresentInElement(
                                    newCost, String.valueOf(i)));
            if (Integer.parseInt(balance.getText().split("\n")[0]) >= Integer.parseInt(newCost.getText().split("\n")[0]) &&
                    !currentCost.getText().equals(newCost.getText())){
                assertTrue("Purchase button should be enabled!\nBalance: " +
                        balance.getText().split("\n")[0] + "\nNew cost: " +
                        newCost.getText().split("\n")[0], doPurchaseButton.getAttribute("class").equals("btn"));
            }
            else{
                assertTrue("Purchase button shouldn't be enabled!\nBalance: " +
                        balance.getText().split("\n")[0] + "\nNew cost: " +
                        newCost.getText().split("\n")[0], doPurchaseButton.getAttribute("class").equals("btn disabled"));
            }
        }

        while (!newCost.getText().equals("0\nруб. в месяц")) {
            String beforeClickCost = newCost.getText();
            try {
                decreaseButton.click();
            } catch (WebDriverException e) {
                JavascriptExecutor jse = (JavascriptExecutor) chromeDriver;
                jse.executeScript("document.getElementsByClassName('decrease')[0].children[0].click()");
            }

            WebDriverWait newCostChangedWait = new WebDriverWait(chromeDriver, 10);
            if (beforeClickCost.equals("300\nруб. в месяц")){
                newCostChangedWait.
                        until(ExpectedConditions.
                                textToBePresentInElement(
                                        newCost, "0\nруб. в месяц"));
            }
            else {
                newCostChangedWait.
                        until(ExpectedConditions.
                                textToBePresentInElement(
                                        newCost, String.valueOf(Integer.parseInt(beforeClickCost.split("\n")[0]) - 50)));
            }
            if (Integer.parseInt(balance.getText().split("\n")[0]) >= Integer.parseInt(newCost.getText().split("\n")[0]) &&
                    !currentCost.getText().equals(newCost.getText())) {
                assertTrue("Purchase button should be enabled!\nBalance: " +
                        balance.getText().split("\n")[0] + "\nNew cost: " +
                        newCost.getText().split("\n")[0], doPurchaseButton.getAttribute("class").equals("btn"));
            } else {
                assertTrue("Purchase button shouldn't be enabled!\nBalance: " +
                        balance.getText().split("\n")[0] + "\nNew cost: " +
                        newCost.getText().split("\n")[0], doPurchaseButton.getAttribute("class").equals("btn disabled"));
            }
        }
    }

    @Test(dataProvider = "paymentData", dataProviderClass = DataProviders.class)
    public void ServicePurchaseTest(String payment){
        paymentAmount.sendKeys(payment);
        doPaymentButton.click();
        WebDriverWait balanceAppearWait = new WebDriverWait(chromeDriver, 10);
        balanceAppearWait.
                until(ExpectedConditions.textToBePresentInElement(balance, payment));

        for (int i=300; i < Integer.parseInt(payment); i = i + 50){
            try{
                increaseButton.click();
            }
            catch (WebDriverException e) {
                JavascriptExecutor jse = (JavascriptExecutor) chromeDriver;
                jse.executeScript("document.getElementsByClassName('increase')[0].children[0].click()");
            }
            WebDriverWait newCostChangedWait = new WebDriverWait(chromeDriver, 10);
            newCostChangedWait.
                    until(ExpectedConditions.
                            textToBePresentInElement(
                                    newCost, String.valueOf(i)));
        }
        String beforePurchaseBalance = balance.getText();
        doPurchaseButton.click();
        WebDriverWait currentCostTextAppearsWait = new WebDriverWait(chromeDriver, 10);
        currentCostTextAppearsWait.until(ExpectedConditions.textToBePresentInElement(currentCost, newCost.getText()));
        assertTrue("Purchase button should not be enabled!\nCurrent cost: " +
                currentCost.getText().split("\n")[0] + "\nNew cost: " +
                newCost.getText().split("\n")[0], doPurchaseButton.getAttribute("class").equals("btn disabled"));
        assertEquals("Current cost and new cost are not equals!", currentCost.getText(), newCost.getText());
        assertEquals("Current speed and new speed are not equals!", currentSpeed.getText(), newSpeed.getText());
        assertEquals("Balance value is not correct!", balance.getText().split("\n")[0],
                String.valueOf(
                        Integer.parseInt(beforePurchaseBalance.split("\n")[0]) - Integer.parseInt(newCost.getText().split("\n")[0])));
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
        doResetButton.click();
        if (!balance.getText().equals("0\nруб.")){
            new WebDriverWait(chromeDriver, 5).
                    until(ExpectedConditions.textToBePresentInElement(balance,"0\nруб."));
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
