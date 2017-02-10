import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Random;

/**
 * Created by dolkons on 10.02.17.
 */
public class RestartServiceTest {

    private ChromeDriver chromeDriver;
    private WebElement paymentAmount;
    private WebElement doPaymentButton;

    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        Utils.runTestSliderService();
        chromeDriver = Utils.initializeChromeDriver();
        paymentAmount = chromeDriver.findElement(By.id("amount"));
        doPaymentButton = chromeDriver.findElement(By.className("actions")).findElements(By.xpath("./*")).get(0);
    }

    @Test
    public void refreshPageTest(){
        paymentAmount.sendKeys(String.valueOf(300 + new Random().nextInt(1400)));
    }

    @Test
    public void restartServiceTest(){

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
