package module.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.google.inject.Provider; // Ensure this is the correct import!

public class ThreadLocalWebDriverProvider implements Provider<WebDriver> {

    private static final ThreadLocal<WebDriver> threadLocalDriver = new ThreadLocal<>();

    @Override
    public WebDriver get() {
        if (threadLocalDriver.get() == null) 
            setupDriver();
        
        return threadLocalDriver.get();
    }

    private static void setupDriver() {
        // You can add logic here to switch between browsers (Chrome, Firefox, etc.)
        ChromeOptions options = new ChromeOptions();
        options.addArguments
		(
			"--no-sandbox",
			"start-maximized", 
			"disable-infobars",
			"--disable-dev-shm-usage",
			"--headless=new"
		);
        
        threadLocalDriver.set(new ChromeDriver(options));
    }

    // Static helper method to cleanly close the driver and prevent memory leaks
    public static void quitDriver() {
        if (threadLocalDriver.get() != null) {
            threadLocalDriver.get().quit(); // 1. Kills the browser
            threadLocalDriver.remove(); // 2. CRITICAL: Wipes the dead reference!
        }
    }
}
