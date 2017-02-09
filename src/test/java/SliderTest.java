import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.io.*;
import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by dolkons on 06.02.17.
 */
public class SliderTest {

    private ChromeDriver chromeDriver;
    private String currentSpeed;
    private String currentCost;
    private String currentSpeedUnits;
    private WebElement increaseButton;

    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        Utils.runTestSliderService();
        chromeDriver = Utils.initializeChromeDriver();
    }

    @BeforeMethod
    public void initializePageElements(){
        currentSpeed = chromeDriver.findElement(
                By.className("main-offer-container")).
                findElement(By.className("speed")).
                findElement(By.tagName("strong")).
                getText();
        currentCost = chromeDriver.findElement(
                By.className("main-offer-container")).
                findElement(By.className("cost")).
                findElement(By.tagName("strong")).
                getText();
        currentSpeedUnits = chromeDriver.findElement(
                By.className("main-offer-container")).
                findElement(By.className("speed")).
                findElement(By.tagName("span")).
                getText();
        increaseButton = chromeDriver.
                findElement(By.className("increase")).
                findElement(By.className("icon"));
    }

    @Test(enabled = true, dataProvider = "tariffsData", dataProviderClass = DataProviders.class)
    public void TestIncrease(String speed, String cost, String speedUnits) throws InterruptedException {
        System.out.println("Starting TestIncrease");

        assertEquals("elements are not equals!", currentSpeed, speed);
        assertEquals("elements are not equals!", currentCost, cost);
        assertEquals("elements are not equals", currentSpeedUnits, speedUnits);

        try{
            increaseButton.click();
        }
        catch (WebDriverException e) {
            JavascriptExecutor jse = (JavascriptExecutor) chromeDriver;
            jse.executeScript("document.getElementsByClassName('increase')[0].children[0].click()");
        }
    }

    @Test(enabled = true, dataProvider = "tariffsData", dataProviderClass = DataProviders.class, dependsOnMethods = "TestIncrease")
    public void TestDecrease(String speed, String cost, String speedUnits) throws InterruptedException {
        System.out.println("Starting TestDecrease");

        assertEquals("elements are not equals!", currentSpeed, speed);
        assertEquals("elements are not equals!", currentCost, cost);
        assertEquals("elements are not equals", currentSpeedUnits, speedUnits);

        try{
            increaseButton.click();
        }
        catch (WebDriverException e) {
            JavascriptExecutor jse = (JavascriptExecutor) chromeDriver;
            jse.executeScript("document.getElementsByClassName('decrease')[0].children[0].click()");
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
