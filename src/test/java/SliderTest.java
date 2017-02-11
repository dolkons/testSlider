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
    private MainPageObject mainPageObject;

    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        Utils.runTestSliderService();
        chromeDriver = Utils.initializeChromeDriver();
        mainPageObject = new MainPageObject(chromeDriver);
    }

    @Test(dataProvider = "tariffsData", dataProviderClass = DataProviders.class)
    public void SliderTestIncrease(String speed, String cost, String speedUnits) throws InterruptedException {

        assertEquals("elements are not equals!", mainPageObject.getNewSpeed().getText().split("\n")[0], speed);
        assertEquals("elements are not equals!", mainPageObject.getNewCost().getText().split("\n")[0], cost);
        assertEquals("elements are not equals", mainPageObject.getNewSpeed().getText().split("\n")[1], speedUnits);

        mainPageObject.clickOnIncreaseButton();
    }

    @Test(dataProvider = "tariffsData", dataProviderClass = DataProviders.class, dependsOnMethods = "SliderTestIncrease")
    public void SliderTestDecrease(String speed, String cost, String speedUnits) throws InterruptedException {

        assertEquals("elements are not equals!", mainPageObject.getNewSpeed().getText().split("\n")[0], speed);
        assertEquals("elements are not equals!", mainPageObject.getNewCost().getText().split("\n")[0], cost);
        assertEquals("elements are not equals", mainPageObject.getNewSpeed().getText().split("\n")[1], speedUnits);

        mainPageObject.clickOnDecreaseButton();
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
