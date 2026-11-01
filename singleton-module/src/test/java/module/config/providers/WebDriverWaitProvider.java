package module.config.providers;

import java.time.Duration;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.inject.Inject;
import com.google.inject.Provider;

import module.config.qualifiers.Driver;

public class WebDriverWaitProvider implements Provider<WebDriverWait> {
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
     * @return a WebDriverWait instance with a timeout of 10 seconds, polling every 500 milliseconds,
     *         and ignoring NoSuchElementException and StaleElementReferenceException.
     */
    @Override
    public WebDriverWait get() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        wait.pollingEvery(Duration.ofMillis(500))
            .ignoring(NoSuchElementException.class)
            .ignoring(StaleElementReferenceException.class);
            
        return wait;
    }
}