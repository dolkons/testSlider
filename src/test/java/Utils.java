import org.apache.commons.io.FileUtils;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.asserts.Assertion;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by dolkons on 07.02.17.
 */
public class Utils {

    private static Process p;
    private static final String SLIDER_SERVICE_DIR = System.getProperty("user.home") + "/test_slider";

    public static void runTestSliderService() throws IOException, InterruptedException {
        System.out.println("Starting Test Slider service...");
        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "java -jar test-slider-1.0.0-SNAPSHOT.jar &");
        builder.directory(new File(SLIDER_SERVICE_DIR));
        builder.redirectErrorStream(true);
        p = builder.start();
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

    public static void deleteDatabase() throws IOException {
        File databaseFolder = new File(SLIDER_SERVICE_DIR + "/sliderDB");
        if (databaseFolder.exists() || databaseFolder.isDirectory()){
            FileUtils.deleteDirectory(databaseFolder);
        }
    }

    public static void stopTestSliderService() throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "jps -l");
        Process process = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while (true) {
            line = r.readLine();
            if (line == null) {
                break;
            }
            else if (line.contains("test-slider")){
                System.out.println(line);
                String pid = line.split(" ")[0];
                ProcessBuilder killProcessBuilder = new ProcessBuilder("/bin/bash", "-c", "sudo kill -9 " + pid);
                Process processKiller = killProcessBuilder.start();
                processKiller.waitFor();
            }
        }
    }

    public static ChromeDriver initializeChromeDriver() {
        ChromeDriver chromeDriver = new ChromeDriver();
        chromeDriver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
        try {
            chromeDriver.get("http://localhost:4567");
        }
        catch (TimeoutException e){
            chromeDriver.quit();
            new Assertion().fail("page load timeout!");
        }
        return chromeDriver;
    }

    public static String createRandomString(int STR_LENGTH) {

        String mCHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        //int STR_LENGTH = 9; // длина генерируемой строки
        Random random = new Random();

        int randomInt = 0;
        char ch;
        StringBuffer randStr = new StringBuffer();
        for (int i = 0; i < STR_LENGTH; i++) {
            randomInt = random.nextInt(mCHAR.length());
            if (randomInt - 1 == -1) {
                ch = mCHAR.charAt(randomInt);
            } else {
                ch = mCHAR.charAt(randomInt - 1);
            }
            randStr.append(ch);
        }
        return randStr.toString();
    }
}
