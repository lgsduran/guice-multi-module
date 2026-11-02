package module.config.providers;

import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.google.inject.Provider;

import java.util.Objects;
import java.util.function.Supplier;

public final class ThreadLocalWebDriverProvider implements Provider<WebDriver> {

    private static final Logger LOGGER = Logger.getLogger(ThreadLocalWebDriverProvider.class.getName());
    private static final ThreadLocal<WebDriver> THREAD_LOCAL_DRIVER = new ThreadLocal<>();
    private final Supplier<WebDriver> driverFactory;

    public ThreadLocalWebDriverProvider() {
        this(ThreadLocalWebDriverProvider::createDriver);
    }

    public ThreadLocalWebDriverProvider(Supplier<WebDriver> driverFactory) {
        this.driverFactory = Objects.requireNonNull(driverFactory);
    }

    /**
     * Provides a WebDriver instance that is unique to the current thread.
     * If the driver is not already created for the current thread, it initializes a new ChromeDriver with specified options.
     *
     * @return a WebDriver instance unique to the current thread
     */
    @Override
    public WebDriver get() {
        WebDriver driver = THREAD_LOCAL_DRIVER.get();
        if (driver == null) {
            LOGGER.info("Creating a WebDriver for the current thread");
            driver = driverFactory.get();
            THREAD_LOCAL_DRIVER.set(driver);
        }
        return driver;
    }

    /**
     * Creates a new ChromeDriver instance with the specified options.
     *
     * @return a new ChromeDriver instance
     */
    private static WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--no-sandbox",
                "--window-size=1920,1080",
                "--disable-dev-shm-usage",
                "--headless=new");
        return new ChromeDriver(options);
    }

    /**
     * Quits the WebDriver instance associated with the current thread and removes it from the ThreadLocal storage.
     * This method should be called when the WebDriver is no longer needed to free up resources.
     */
    public static void quitDriver() {
        WebDriver driver = THREAD_LOCAL_DRIVER.get();
        if (driver != null) {
            try {
                LOGGER.info("Quitting the WebDriver for the current thread");
                driver.quit();
            } finally {
                THREAD_LOCAL_DRIVER.remove();
            }
        }
    }

    public static WebDriver currentDriver() {
        return THREAD_LOCAL_DRIVER.get();
    }
}