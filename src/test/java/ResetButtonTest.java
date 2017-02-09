import org.junit.BeforeClass;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created by dolkons on 09.02.17.
 */
public class ResetButtonTest {

    private ChromeDriver chromeDriver;

    @BeforeClass
    public void setup(){
        chromeDriver = Utils.initializeChromeDriver();
    }

}

