package module.config.providers;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;
import java.util.logging.Logger;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.inject.Inject;
import com.google.inject.Provider;

import module.config.qualifiers.Driver;

public class WebDriverWaitProvider implements Provider<WebDriverWait> {
    private static final Logger LOGGER = Logger.getLogger(WebDriverWaitProvider.class.getName());
    private static final String CONFIG_FILE = "config.properties";
    private static final String TIMEOUT_PROPERTY = "webdriver.wait.timeout.seconds";
    private static final Duration TIMEOUT = loadTimeout();

    private final WebDriver driver;

    /**
     * Creates a new instance of {@link WebDriverWaitProvider}.
     *
     * @param driver the WebDriver instance to be used for waiting
     */
    @Inject
    public WebDriverWaitProvider(@Driver WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Provides a configured WebDriverWait instance.
     *
     * @return a WebDriverWait instance with the configured timeout, polling every 500 milliseconds,
     *         and ignoring NoSuchElementException and StaleElementReferenceException.
     */
    @Override
    public WebDriverWait get() {
        LOGGER.info(() -> "Creating WebDriverWait with timeout of " + TIMEOUT.toSeconds() + " seconds");
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        
        wait.pollingEvery(Duration.ofMillis(500))
            .ignoring(NoSuchElementException.class)
            .ignoring(StaleElementReferenceException.class);
            
        return wait;
    }

    private static Duration loadTimeout() {
        Properties properties = new Properties();
        try (InputStream input = WebDriverWaitProvider.class.getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Missing configuration resource: " + CONFIG_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read configuration resource: " + CONFIG_FILE, e);
        }

        String value = properties.getProperty(TIMEOUT_PROPERTY);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing configuration property: " + TIMEOUT_PROPERTY);
        }

        try {
            long seconds = Long.parseLong(value.trim());
            if (seconds <= 0) {
                throw new IllegalStateException("Configuration property must be positive: " + TIMEOUT_PROPERTY);
            }
            return Duration.ofSeconds(seconds);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Configuration property must be an integer: " + TIMEOUT_PROPERTY, e);
        }
    }
}