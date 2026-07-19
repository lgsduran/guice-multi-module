package module.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import module.config.BaseUrl;
import module.config.WebDriverModule;

public abstract class BaseTest {
    protected Injector injector;
    protected WebDriver driver;

    @Inject
    @BaseUrl
    private String baseUrl;

    @BeforeEach
    public void setUp() {
        // Create the Guice injector
        injector = Guice.createInjector(new WebDriverModule());
        injector.injectMembers(this);
        driver = injector.getInstance(WebDriver.class);
        driver.navigate().to(baseUrl); // Navigate to the base URL before each test
    }

    @AfterEach
    public void tearDown() {
        if (this.driver != null) {
            this.driver.quit(); // Closes the browser and ends the session
        }
    }
}
