import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.DataProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Created by dolkons on 07.02.17.
 */
public class Utils {

    public static void runTestSliderService() throws IOException {
        System.out.println("Starting Test Slider service...");
        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "java -jar test-slider-1.0.0-SNAPSHOT.jar &");
        builder.directory(new File(System.getProperty( "user.home" ) + "/test_slider"));
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) {
                break;
            }
            System.out.println(line);
        }
    }

    public static ChromeDriver initializeChromeDriver() {
        ChromeDriver chromeDriver = new ChromeDriver();
        chromeDriver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        chromeDriver.get("http://localhost:4567");
        return chromeDriver;
    }
}
