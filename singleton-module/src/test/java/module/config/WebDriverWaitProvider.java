package module.config;

import java.time.Duration;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class WebDriverWaitProvider implements Provider<WebDriverWait> {
    private final WebDriver driver;

    @Inject
    public WebDriverWaitProvider(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public WebDriverWait get() {
        return (WebDriverWait) new WebDriverWait(this.driver, Duration.ofSeconds(10))
            .withTimeout(Duration.ofSeconds(10))        
            .pollingEvery(Duration.ofSeconds(2))
            .ignoring(NoSuchElementException.class)
            .ignoring(StaleElementReferenceException.class);

    }
}