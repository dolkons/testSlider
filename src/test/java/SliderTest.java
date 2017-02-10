import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by dolkons on 06.02.17.
 */
public class SliderTest {

    private ChromeDriver chromeDriver;
    private MainPage mainPage;

    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        Utils.runTestSliderService();
        chromeDriver = Utils.initializeChromeDriver();
        mainPage = new MainPage(chromeDriver);
    }

    @Test(enabled = true, dataProvider = "tariffsData", dataProviderClass = DataProviders.class)
    public void TestIncrease(String speed, String cost, String speedUnits) throws InterruptedException {
        System.out.println("Starting TestIncrease");

        assertEquals("elements are not equals!", mainPage.getCurrentSpeed().getText(), speed);
        assertEquals("elements are not equals!", mainPage.getCurrentCost().getText(), cost);
        assertEquals("elements are not equals", mainPage.getCurrentSpeed().getText().split("\n")[1], speedUnits);

        mainPage.clickOnIncreaseButton();
    }

    @Test(enabled = true, dataProvider = "tariffsData", dataProviderClass = DataProviders.class, dependsOnMethods = "TestIncrease")
    public void TestDecrease(String speed, String cost, String speedUnits) throws InterruptedException {
        System.out.println("Starting TestDecrease");

        assertEquals("elements are not equals!", mainPage.getCurrentSpeed().getText(), speed);
        assertEquals("elements are not equals!", mainPage.getCurrentCost().getText(), cost);
        assertEquals("elements are not equals", mainPage.getCurrentSpeed().getText().split("\n")[1], speedUnits);

        mainPage.clickOnDecreaseButton();
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

    @AfterClass(enabled = true)
    public void finish() throws IOException, InterruptedException {
        if (chromeDriver != null) {
            chromeDriver.quit();
        }
        Utils.stopTestSliderService();
        Utils.deleteDatabase();
    }
}
