import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by dolkons on 08.02.17.
 */
public class MainWindowTest {

    private ChromeDriver chromeDriver;
    private MainPageObject mainPageObject;

    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        Utils.runTestSliderService();
        chromeDriver = Utils.initializeChromeDriver();
        mainPageObject = new MainPageObject(chromeDriver);
    }

    @Test
    public void mainWindowTest(){
        assertTrue(mainPageObject.getDoPurchaseButton().getAttribute("class").equals("btn disabled"), "Purchase button is enabled!");

        assertEquals(mainPageObject.getCurrentTime().getText(), "0\nдней осталось", "String are not equal!");
        assertEquals(mainPageObject.getCurrentSpeed().getText(), "64\nКбит/сек (макс.)","String are not equal!");
        assertEquals(mainPageObject.getCurrentCost().getText(), "0\nруб. в месяц", "String are not equal!");

        assertEquals(mainPageObject.getNewTime().getText(), "30\nдней останется", "String are not equal!");
        assertEquals(mainPageObject.getNewSpeed().getText(), "64\nКбит/сек (макс.)","String are not equal!");
        assertEquals(mainPageObject.getNewCost().getText(), "0\nруб. в месяц", "String are not equal!");

        assertEquals(mainPageObject.getBalance().getText(), "0\nруб.","String are not equal!");
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
