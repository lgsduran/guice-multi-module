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

@Guice(modules = WebDriverModule.class)
public class ParallelSearchTest {

    @Inject
    private Provider<WebDriver> driverProvider;

    @Test
    public void testGoogle() {
        WebDriver driver = driverProvider.get();
        driver.get("https://www.google.com");
        Assert.assertEquals(driver.getTitle(), "Google");
    }

    @Test
    public void testBing() {
        WebDriver driver = driverProvider.get();
        driver.get("https://www.bing.com");
        Assert.assertTrue(driver.getTitle().contains("Bing"));
    }

    @AfterMethod
    public void tearDown() {
        ThreadLocalWebDriverProvider.quitDriver();
    }
}