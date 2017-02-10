import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

/**
 * Created by dolkons on 10.02.17.
 */
public class MainPage {

    private WebDriver driver;

    private WebElement paymentAmount;
    private WebElement doPaymentButton;
    private WebElement balance;
    private WebElement increaseButton;
    private WebElement slider;
    private WebElement doResetButton;
    private WebElement newCost;
    private WebElement doPurchaseButton;
    private WebElement currentCost;
    private WebElement currentSpeed;

    public MainPage(WebDriver driver) {
        this.driver = driver;
    }

    public WebElement getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(WebElement paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public WebElement getDoPaymentButton() {
        return doPaymentButton;
    }

    public void setDoPaymentButton(WebElement doPaymentButton) {
        this.doPaymentButton = doPaymentButton;
    }

    public WebElement getBalance() {
        return balance;
    }

    public void setBalance(WebElement balance) {
        this.balance = balance;
    }

    public WebElement getIncreaseButton() {
        return increaseButton;
    }

    public void setIncreaseButton(WebElement increaseButton) {
        this.increaseButton = increaseButton;
    }

    public WebElement getSlider() {
        return slider;
    }

    public void setSlider(WebElement slider) {
        this.slider = slider;
    }

    public WebElement getDoResetButton() {
        return doResetButton;
    }

    public void setDoResetButton(WebElement doResetButton) {
        this.doResetButton = doResetButton;
    }

    public WebElement getNewCost() {
        return newCost;
    }

    public void setNewCost(WebElement newCost) {
        this.newCost = newCost;
    }

    public WebElement getDoPurchaseButton() {
        return doPurchaseButton;
    }

    public void setDoPurchaseButton(WebElement doPurchaseButton) {
        this.doPurchaseButton = doPurchaseButton;
    }

    public WebElement getCurrentCost() {
        return currentCost;
    }

    public void setCurrentCost(WebElement currentCost) {
        this.currentCost = currentCost;
    }

    public WebElement getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(WebElement currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public void clickOnIncreaseButton(){
        try {
            increaseButton.click();
        } catch (WebDriverException e) {
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            jse.executeScript("document.getElementsByClassName('increase')[0].children[0].click()");
        }
    }
}
