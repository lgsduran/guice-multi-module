package module.config;

import org.openqa.selenium.WebDriver;

import com.google.inject.AbstractModule;

public class WebDriverModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(WebDriver.class).toProvider(ThreadLocalWebDriverProvider.class);
    }
}
