package module.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.google.inject.Provider;

public class WebDriverProvider implements Provider<WebDriver> {
    private WebDriver driver;

    @Override
    public WebDriver get() {
        if (driver == null) {
            // Initializes a single ChromeDriver instance to be shared
            ChromeOptions options = new ChromeOptions();
            options.addArguments(
                "--no-sandbox",
                "start-maximized", 
                "disable-infobars",
                "--disable-dev-shm-usage",
                "--headless=new"
            );
            driver = new ChromeDriver(options);
        }
        return driver;
    }
   
}
