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
    private MainPage mainPage;

    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        Utils.runTestSliderService();
        chromeDriver = Utils.initializeChromeDriver();
        mainPage = new MainPage(chromeDriver);
    }

    @Test(dataProvider = "paymentData", dataProviderClass = DataProviders.class)
    public void PurchaseButtonStateTest(String payment){
        mainPage.doPayment(payment);
        WebDriverWait balanceAppearWait = new WebDriverWait(chromeDriver, 10);
        balanceAppearWait.
                until(ExpectedConditions.textToBePresentInElement(mainPage.getBalance(), payment));

        for (int i=300; i < 300 + (50 * new Random().nextInt(22)); i = i + 50){
            mainPage.clickOnIncreaseButton();
            WebDriverWait newCostChangedWait = new WebDriverWait(chromeDriver, 10);
            newCostChangedWait.
                    until(ExpectedConditions.
                            textToBePresentInElement(
                                    mainPage.getNewCost(), String.valueOf(i)));

            if (Integer.parseInt(
                    mainPage.getBalance().getText().split("\n")[0]) >= Integer.parseInt(mainPage.getNewCost().getText().split("\n")[0]) &&
                    !mainPage.getCurrentCost().getText().equals(mainPage.getNewCost().getText())){
                assertTrue("Purchase button should be enabled!\nBalance: " +
                        mainPage.getBalance().getText().split("\n")[0] + "\nNew cost: " +
                        mainPage.getNewCost().getText().split("\n")[0], mainPage.getDoPurchaseButton().getAttribute("class").equals("btn"));
            }
            else{
                assertTrue("Purchase button shouldn't be enabled!\nBalance: " +
                        mainPage.getBalance().getText().split("\n")[0] + "\nNew cost: " +
                        mainPage.getNewCost().getText().split("\n")[0],
                        mainPage.getDoPurchaseButton().getAttribute("class").equals("btn disabled"));
            }
        }

        while (!mainPage.getNewCost().getText().equals("0\nруб. в месяц")) {
            String beforeClickCost = mainPage.getNewCost().getText();
            mainPage.clickOnDecreaseButton();

            WebDriverWait newCostChangedWait = new WebDriverWait(chromeDriver, 10);
            if (beforeClickCost.equals("300\nруб. в месяц")){
                newCostChangedWait.
                        until(ExpectedConditions.
                                textToBePresentInElement(
                                        mainPage.getNewCost(), "0\nруб. в месяц"));
            }
            else {
                newCostChangedWait.
                        until(ExpectedConditions.
                                textToBePresentInElement(
                                        mainPage.getNewCost(), String.valueOf(Integer.parseInt(beforeClickCost.split("\n")[0]) - 50)));
            }
            if (Integer.parseInt(
                    mainPage.getBalance().getText().split("\n")[0]) >= Integer.parseInt(mainPage.getNewCost().getText().split("\n")[0]) &&
                    !mainPage.getCurrentCost().getText().equals(mainPage.getNewCost().getText())) {
                assertTrue("Purchase button should be enabled!\nBalance: " +
                        mainPage.getBalance().getText().split("\n")[0] + "\nNew cost: " +
                        mainPage.getNewCost().getText().split("\n")[0],
                        mainPage.getDoPurchaseButton().getAttribute("class").equals("btn"));
            } else {
                assertTrue("Purchase button shouldn't be enabled!\nBalance: " +
                        mainPage.getBalance().getText().split("\n")[0] + "\nNew cost: " +
                        mainPage.getNewCost().getText().split("\n")[0],
                        mainPage.getDoPurchaseButton().getAttribute("class").equals("btn disabled"));
            }
        }
    }

    @Test(dataProvider = "paymentData", dataProviderClass = DataProviders.class)
    public void ServicePurchaseTest(String payment){
        mainPage.doPayment(payment);
        WebDriverWait balanceAppearWait = new WebDriverWait(chromeDriver, 10);
        balanceAppearWait.
                until(ExpectedConditions.textToBePresentInElement(mainPage.getBalance(), payment));

        for (int i=300; i < Integer.parseInt(payment); i = i + 50){
            mainPage.clickOnIncreaseButton();
            WebDriverWait newCostChangedWait = new WebDriverWait(chromeDriver, 10);
            newCostChangedWait.
                    until(ExpectedConditions.
                            textToBePresentInElement(
                                    mainPage.getNewCost(), String.valueOf(i)));
        }
        String beforePurchaseBalance = mainPage.getBalance().getText();
        mainPage.clickOnPurchaseButton();
        WebDriverWait currentCostTextAppearsWait = new WebDriverWait(chromeDriver, 10);
        currentCostTextAppearsWait.until(ExpectedConditions.textToBePresentInElement(mainPage.getCurrentCost(), mainPage.getNewCost().getText()));
        assertTrue("Purchase button should not be enabled!\nCurrent cost: " +
                mainPage.getCurrentCost().getText().split("\n")[0] + "\nNew cost: " +
                mainPage.getNewCost().getText().split("\n")[0], mainPage.getDoPurchaseButton().getAttribute("class").equals("btn disabled"));
        assertEquals("Current cost and new cost are not equals!", mainPage.getCurrentCost().getText(), mainPage.getNewCost().getText());
        assertEquals("Current speed and new speed are not equals!", mainPage.getCurrentSpeed().getText(), mainPage.getNewSpeed().getText());
        assertEquals("Balance value is not correct!", mainPage.getBalance().getText().split("\n")[0],
                String.valueOf(
                        Integer.parseInt(beforePurchaseBalance.split("\n")[0]) - Integer.parseInt(mainPage.getNewCost().getText().split("\n")[0])));
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

    @AfterClass
    public void finish() throws IOException, InterruptedException {
        if (chromeDriver != null) {
            chromeDriver.quit();
        }
        Utils.stopTestSliderService();
        Utils.deleteDatabase();
    }
}
