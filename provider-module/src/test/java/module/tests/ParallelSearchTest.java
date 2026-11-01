package module.tests;

import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import module.config.ThreadLocalWebDriverProvider;
import module.config.WebDriverModule;

@Guice(modules = WebDriverModule.class)
public class ParallelSearchTest {
    private static final Logger LOGGER = Logger.getLogger(ParallelSearchTest.class.getName());

    @Inject
    private Provider<WebDriver> driverProvider;

    @Test
    public void testGoogle() {
        LOGGER.info("Starting Google search test");
        WebDriver driver = driverProvider.get();
        driver.get("https://www.google.com");
        Assert.assertEquals(driver.getTitle(), "Google");
        LOGGER.info("Google search test completed");
    }

    @Test
    public void testBing() {
        LOGGER.info("Starting Bing search test");
        WebDriver driver = driverProvider.get();
        driver.get("https://www.bing.com");
        Assert.assertTrue(driver.getTitle().contains("Bing"));
        LOGGER.info("Bing search test completed");
    }

    @AfterMethod
    public void tearDown() {
        LOGGER.info("Closing the WebDriver for the current thread");
        ThreadLocalWebDriverProvider.quitDriver();
    }
}