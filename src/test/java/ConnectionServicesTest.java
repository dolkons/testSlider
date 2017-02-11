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
    private MainPageObject mainPageObject;

    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        Utils.runTestSliderService();
        chromeDriver = Utils.initializeChromeDriver();
        mainPageObject = new MainPageObject(chromeDriver);
    }

    @Test(dataProvider = "paymentData", dataProviderClass = DataProviders.class)
    public void PurchaseButtonStateTest(String payment){
        mainPageObject.doPayment(payment);
        WebDriverWait balanceAppearWait = new WebDriverWait(chromeDriver, 10);
        balanceAppearWait.
                until(ExpectedConditions.textToBePresentInElement(mainPageObject.getBalance(), payment));

        for (int i=300; i < 300 + (50 * new Random().nextInt(22)); i = i + 50){
            mainPageObject.clickOnIncreaseButton();
            WebDriverWait newCostChangedWait = new WebDriverWait(chromeDriver, 10);
            newCostChangedWait.
                    until(ExpectedConditions.
                            textToBePresentInElement(
                                    mainPageObject.getNewCost(), String.valueOf(i)));

            if (Integer.parseInt(
                    mainPageObject.getBalance().getText().split("\n")[0]) >= Integer.parseInt(mainPageObject.getNewCost().getText().split("\n")[0]) &&
                    !mainPageObject.getCurrentCost().getText().equals(mainPageObject.getNewCost().getText())){
                assertTrue("Purchase button should be enabled!\nBalance: " +
                        mainPageObject.getBalance().getText().split("\n")[0] + "\nNew cost: " +
                        mainPageObject.getNewCost().getText().split("\n")[0], mainPageObject.getDoPurchaseButton().getAttribute("class").equals("btn"));
            }
            else{
                assertTrue("Purchase button shouldn't be enabled!\nBalance: " +
                        mainPageObject.getBalance().getText().split("\n")[0] + "\nNew cost: " +
                        mainPageObject.getNewCost().getText().split("\n")[0],
                        mainPageObject.getDoPurchaseButton().getAttribute("class").equals("btn disabled"));
            }
        }

        while (!mainPageObject.getNewCost().getText().equals("0\nруб. в месяц")) {
            String beforeClickCost = mainPageObject.getNewCost().getText();
            mainPageObject.clickOnDecreaseButton();

            WebDriverWait newCostChangedWait = new WebDriverWait(chromeDriver, 10);
            if (beforeClickCost.equals("300\nруб. в месяц")){
                newCostChangedWait.
                        until(ExpectedConditions.
                                textToBePresentInElement(
                                        mainPageObject.getNewCost(), "0\nруб. в месяц"));
            }
            else {
                newCostChangedWait.
                        until(ExpectedConditions.
                                textToBePresentInElement(
                                        mainPageObject.getNewCost(), String.valueOf(Integer.parseInt(beforeClickCost.split("\n")[0]) - 50)));
            }
            if (Integer.parseInt(
                    mainPageObject.getBalance().getText().split("\n")[0]) >= Integer.parseInt(mainPageObject.getNewCost().getText().split("\n")[0]) &&
                    !mainPageObject.getCurrentCost().getText().equals(mainPageObject.getNewCost().getText())) {
                assertTrue("Purchase button should be enabled!\nBalance: " +
                        mainPageObject.getBalance().getText().split("\n")[0] + "\nNew cost: " +
                        mainPageObject.getNewCost().getText().split("\n")[0],
                        mainPageObject.getDoPurchaseButton().getAttribute("class").equals("btn"));
            } else {
                assertTrue("Purchase button shouldn't be enabled!\nBalance: " +
                        mainPageObject.getBalance().getText().split("\n")[0] + "\nNew cost: " +
                        mainPageObject.getNewCost().getText().split("\n")[0],
                        mainPageObject.getDoPurchaseButton().getAttribute("class").equals("btn disabled"));
            }
        }
    }

    @Test(dataProvider = "paymentData", dataProviderClass = DataProviders.class)
    public void ServicePurchaseTest(String payment){
        for (int i=300; i < Integer.parseInt(payment); i = i + 50){
            mainPageObject.clickOnIncreaseButton();
            WebDriverWait newCostChangedWait = new WebDriverWait(chromeDriver, 10);
            newCostChangedWait.
                    until(ExpectedConditions.
                            textToBePresentInElement(
                                    mainPageObject.getNewCost(), String.valueOf(i)));
        }
        assertEquals("Purchase button disabled, but current tariff's speed value is not default!",
                mainPageObject.getCurrentSpeed().getText(), "64\nКбит/сек (макс.)");
        assertEquals("Purchase button disabled, but current tariff's cost value is not default!",
                mainPageObject.getCurrentCost().getText(), "0\nруб. в месяц");

        mainPageObject.doPayment(payment);
        WebDriverWait balanceAppearWait = new WebDriverWait(chromeDriver, 10);
        balanceAppearWait.
                until(ExpectedConditions.textToBePresentInElement(mainPageObject.getBalance(), payment));

        String beforePurchaseBalance = mainPageObject.getBalance().getText();
        mainPageObject.clickOnPurchaseButton();
        WebDriverWait currentCostTextAppearsWait = new WebDriverWait(chromeDriver, 10);
        currentCostTextAppearsWait.until(ExpectedConditions.textToBePresentInElement(mainPageObject.getCurrentCost(), mainPageObject.getNewCost().getText()));
        assertTrue("Purchase button should not be enabled!\nCurrent cost: " +
                mainPageObject.getCurrentCost().getText().split("\n")[0] + "\nNew cost: " +
                mainPageObject.getNewCost().getText().split("\n")[0], mainPageObject.getDoPurchaseButton().getAttribute("class").equals("btn disabled"));
        assertEquals("Current cost and new cost are not equals!", mainPageObject.getCurrentCost().getText(), mainPageObject.getNewCost().getText());
        assertEquals("Current speed and new speed are not equals!", mainPageObject.getCurrentSpeed().getText(), mainPageObject.getNewSpeed().getText());
        assertEquals("Balance value is invalid!", mainPageObject.getBalance().getText().split("\n")[0],
                String.valueOf(
                        Integer.parseInt(beforePurchaseBalance.split("\n")[0]) - Integer.parseInt(mainPageObject.getNewCost().getText().split("\n")[0])));
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
        mainPageObject.clickOnResetButton();
        if (!mainPageObject.getBalance().getText().equals("0\nруб.")){
            new WebDriverWait(chromeDriver, 5).
                    until(ExpectedConditions.textToBePresentInElement(mainPageObject.getBalance(),"0\nруб."));
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
