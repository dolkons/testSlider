import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.*;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Created by dolkons on 06.02.17.
 */
public class FirstTest {

    private ChromeDriver chromeDriver;

    @BeforeMethod
    public void setup() throws IOException {
        Utils.runTestSliderService();
        chromeDriver = Utils.initializeChromeDriver();
    }

    @Test(enabled = true)
    public void Test1() throws InterruptedException {
        System.out.println("Starting Test1");
        String currentSpeed;
        do{
            currentSpeed = chromeDriver.findElement(
                    By.className("main-offer-container")).
                    findElement(By.className("speed")).
                    findElement(By.tagName("strong")).
                    getText();
            Thread.sleep(100);
            WebElement increaseButton = chromeDriver.
                    findElement(By.className("increase")).
                    findElement(By.className("icon"));

            new WebDriverWait(chromeDriver, 10).
                    until(ExpectedConditions.
                            elementToBeClickable(increaseButton));

            try {
                increaseButton.click();
            }

            catch (WebDriverException e){
                JavascriptExecutor jse = (JavascriptExecutor) chromeDriver;
                jse.executeScript("document.getElementsByClassName('increase')[0].children[0].click()");
            }
        }
        while (!currentSpeed.equals("Макс"));

        do{
            currentSpeed = chromeDriver.findElement(
                    By.className("main-offer-container")).
                    findElement(By.className("speed")).
                    findElement(By.tagName("strong")).
                    getText();
            Thread.sleep(100);

            WebElement decreaseButton = chromeDriver.
                    findElement(By.className("decrease")).
                    findElement(By.className("icon"));

            new WebDriverWait(chromeDriver, 10).
                    until(ExpectedConditions.
                            elementToBeClickable(decreaseButton));

            try{
                decreaseButton.click();
            }
            catch (WebDriverException e) {
                JavascriptExecutor jse = (JavascriptExecutor) chromeDriver;
                jse.executeScript("document.getElementsByClassName('decrease')[0].children[0].click()");
            }
        }
        while (!currentSpeed.equals("64"));

        Thread.sleep(5000);
    }

    @AfterClass(enabled = true)
    public void finish(){
        chromeDriver.close();
    }
}
