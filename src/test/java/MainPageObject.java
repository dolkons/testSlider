import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.Assertion;

/**
 * Created by dolkons on 10.02.17.
 */
public class MainPageObject {

    private WebDriver driver;

    @FindBy(id = "amount")
    private WebElement paymentAmount;

    @FindBy(xpath = "//*[@class='actions']/*[1]")
    private WebElement doPaymentButton;

    @FindBy(id = "balance-holder")
    private WebElement balance;

    @FindBy(xpath = "//*[@class='increase']/*")
    private WebElement increaseButton;

    @FindBy(xpath = "//*[@class='decrease']/*")
    private WebElement decreaseButton;

    @FindBy(id = "slider-handle")
    private WebElement slider;

    @FindBy(xpath = "//*[@class='actions']/*[2]")
    private WebElement doResetButton;

    @FindBy(xpath = "//*[@class='tarriff-info']/a")
    private WebElement doPurchaseButton;

    @FindBy(xpath = "//*[@class='tarriff-info']/div[3]")
    private WebElement currentCost;

    @FindBy(xpath = "//*[@class='cost']")
    private WebElement newCost;

    @FindBy(xpath = "//*[@class='tariff unavailable']/*/*[@class='speed']")
    private WebElement currentSpeed;

    @FindBy(xpath = "//*[@class='tariff']/*/*[@class='speed']")
    private WebElement newSpeed;

    @FindBy(xpath = "//*[@class='tariff unavailable']/*/*[@class='time']")
    private WebElement currentTime;

    @FindBy(xpath = "//*[@class='tariff']/*/*[@class='time']")
    private WebElement newTime;

    private Point maxLeftSliderLocation;

    public MainPageObject(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        maxLeftSliderLocation = getSlider().getLocation();
    }

    public Point getMaxLeftSliderLocation() {
        return maxLeftSliderLocation;
    }

    public WebElement getNewSpeed() {
        return newSpeed;
    }

    public WebElement getCurrentTime() {
        return currentTime;
    }

    public WebElement getNewTime() {
        return newTime;
    }

    public WebElement getPaymentAmount() {
        return paymentAmount;
    }

    public WebElement getDoPaymentButton() {
        return doPaymentButton;
    }

    public WebElement getBalance() {
        return balance;
    }

    public WebElement getIncreaseButton() {
        return increaseButton;
    }

    public WebElement getSlider() {
        return slider;
    }

    public WebElement getDoResetButton() {
        return doResetButton;
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

    public void clickOnDecreaseButton(){
        try {
            decreaseButton.click();
        } catch (WebDriverException e) {
            JavascriptExecutor jse = (JavascriptExecutor) driver;
            jse.executeScript("document.getElementsByClassName('decrease')[0].children[0].click()");
        }
    }

    public void doPayment(String payment){
        paymentAmount.clear();
        paymentAmount.sendKeys(payment);

        String currentBalance = this.getBalance().getText().split("\n")[0];
        doPaymentButton.click();
        WebDriverWait paymentWait = new WebDriverWait(driver, 5);
        try {
            paymentWait.until(
                    ExpectedConditions.textToBePresentInElement(
                            this.balance, String.valueOf(Integer.parseInt(payment) + Integer.parseInt(currentBalance)) + "\nруб."));
        }
        catch (NumberFormatException e){
            return;
        }
        catch (TimeoutException e){
            e.printStackTrace();
            new Assertion().fail("Old balance: " + currentBalance + "\n" +
                    "Payment: " + payment + "\n" + "Balance should be: " + "\n" +
                    String.valueOf(Integer.parseInt(payment) + Integer.parseInt(currentBalance)));
        }
    }

    public void clickOnResetButton(){
        doResetButton.click();
    }

    public void clickOnPurchaseButton(){
        doPurchaseButton.click();
    }
}
