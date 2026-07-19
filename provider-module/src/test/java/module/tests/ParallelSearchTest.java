package module.tests;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

import module.config.ThreadLocalWebDriverProvider;
import module.config.WebDriverModule;

// 2. Tell TestNG to use your Guice module to inject dependencies
@Guice(modules = WebDriverModule.class)
public class ParallelSearchTest {

   // 1. Change this from WebDriver to Provider<WebDriver>
    @Inject
    private Provider<WebDriver> driverProvider; 

    @Test
    public void testGoogle() {
        // 2. Fetch the active browser for this specific test method
        WebDriver driver = driverProvider.get();
        
        driver.get("https://www.google.com");
        Assert.assertEquals(driver.getTitle(), "Google");
    }

    @Test
    public void testBing() {
        // Fetch the active browser for this specific test method
        WebDriver driver = driverProvider.get();
        
        driver.get("https://www.bing.com");
        Assert.assertTrue(driver.getTitle().contains("Bing"));
    }

    @AfterMethod
    public void tearDown() {
        ThreadLocalWebDriverProvider.quitDriver();
    }
}