package module.tests;

import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import module.config.SearchUrlModule;
import module.config.WebDriverModule;
import module.config.providers.ThreadLocalWebDriverProvider;
import module.config.qualifiers.BingUrl;
import module.config.qualifiers.GoogleUrl;

@Guice(modules = { WebDriverModule.class, SearchUrlModule.class })
public class ParallelSearchTest {
    private static final Logger LOGGER = Logger.getLogger(ParallelSearchTest.class.getName());

    @Inject
    private Provider<WebDriver> driverProvider;

    @Inject
    @GoogleUrl
    private String googleUrl;

    @Inject
    @BingUrl
    private String bingUrl;

    @Test
    public void testGoogle() {
        LOGGER.info(() -> "Starting Google search test on " + Thread.currentThread().getName());
        WebDriver driver = driverProvider.get();
        driver.get(googleUrl);
        Assert.assertEquals(driver.getTitle(), "Google");
        LOGGER.info(() -> "Google search test completed on " + Thread.currentThread().getName());
    }

    @Test
    public void testBing() {
        LOGGER.info(() -> "Starting Bing search test on " + Thread.currentThread().getName());
        WebDriver driver = driverProvider.get();
        driver.get(bingUrl);
        Assert.assertTrue(driver.getTitle().contains("Bing"));
        LOGGER.info(() -> "Bing search test completed on " + Thread.currentThread().getName());
    }

    @AfterMethod
    public void tearDown() {
        LOGGER.info("Closing the WebDriver for the current thread");
        ThreadLocalWebDriverProvider.quitDriver();
    }
}