import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by dolkons on 08.02.17.
 */
public class MainWindowTest {

    private ChromeDriver chromeDriver;
    private String newTime;
    private String newSpeed;
    private String newCost;
    private String currentTime;
    private String currentSpeed;
    private String currentCost;
    private String balance;
    private WebElement increaseButton;
    private WebElement doPurchaseButton;
    private WebElement doPaymentButton;
    private WebElement doResetButton;

    @BeforeClass
    public void setup() throws IOException, InterruptedException {
        Utils.runTestSliderService();
        chromeDriver = Utils.initializeChromeDriver();
        newTime = chromeDriver.
                findElement(By.className("main-offer-container")).
                findElement(By.className("time")).
                getText();
        newSpeed = chromeDriver.findElement(
                By.className("main-offer-container")).
                findElement(By.className("speed")).
                getText();
        newCost = chromeDriver.findElement(
                By.className("main-offer-container")).
                findElement(By.className("cost")).
                getText();
        doPurchaseButton = chromeDriver.
                findElement(By.xpath("//*[@id=\"sliders\"]/div[2]/div[3]/div[2]/div/div/div/a"));
        currentTime = chromeDriver.
                findElement(By.className("content-container")).
                findElement(By.className("time")).
                getText();
        currentSpeed = chromeDriver.
                findElement(By.className("content-container")).
                findElement(By.className("speed")).
                getText();
        currentCost = chromeDriver.
                findElement(By.className("content-container")).
                findElement(By.className("cost")).
                getText();
        balance = chromeDriver.findElement(By.id("balance-holder")).getText();
        doPaymentButton = chromeDriver.findElement(By.className("actions")).findElements(By.xpath("./*")).get(0);
        doResetButton = chromeDriver.findElement(By.className("actions")).findElements(By.xpath("./*")).get(1);
    }

    @Test
    public void mainWindowTest(){
        assertTrue(doPurchaseButton.getAttribute("class").equals("btn disabled"), "Purchase button is enabled!");

        assertEquals(currentTime, "0\nдней осталось", "String are not equal!");
        assertEquals(currentSpeed, "64\nКбит/сек (макс.)","String are not equal!");
        assertEquals(currentCost, "0\nруб. в месяц", "String are not equal!");

        assertEquals(newTime, "30\nдней останется", "String are not equal!");
        assertEquals(newSpeed, "64\nКбит/сек (макс.)","String are not equal!");
        assertEquals(newCost, "0\nруб. в месяц", "String are not equal!");

        assertEquals(balance, "0\nруб.","String are not equal!");
    }

    @AfterClass(alwaysRun = true)
    public void finish(){
        chromeDriver.close();
    }
}
