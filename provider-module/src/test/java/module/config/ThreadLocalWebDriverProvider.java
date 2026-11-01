package module.config;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.google.inject.Provider;

public class ThreadLocalWebDriverProvider implements Provider<WebDriver> {

    private static final ThreadLocal<WebDriver> THREAD_LOCAL_DRIVER = new ThreadLocal<>();

    @Override
    public WebDriver get() {
        WebDriver driver = THREAD_LOCAL_DRIVER.get();
        if (driver == null) {
            driver = createDriver();
            THREAD_LOCAL_DRIVER.set(driver);
        }
        return driver;
    }

    private static WebDriver createDriver() {
        Logger.getLogger("org.openqa.selenium.devtools.CdpVersionFinder").setLevel(Level.SEVERE);
        Logger.getLogger("org.openqa.selenium.chromium.ChromiumDriver").setLevel(Level.SEVERE);

        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--no-sandbox",
                "start-maximized",
                "disable-infobars",
                "--disable-dev-shm-usage",
                "--headless=new");
        return new ChromeDriver(options);
    }

    public static void quitDriver() {
        WebDriver driver = THREAD_LOCAL_DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                THREAD_LOCAL_DRIVER.remove();
            }
        }
    }
}
