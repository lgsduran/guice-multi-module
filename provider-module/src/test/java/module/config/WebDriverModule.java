package module.config;

import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;

import com.google.inject.AbstractModule;

public final class WebDriverModule extends AbstractModule {
    private static final Logger LOGGER = Logger.getLogger(WebDriverModule.class.getName());

    /**
     * Configures the bindings for WebDriver.
     * The WebDriver is bound to a provider that creates thread-local instances.
     */
    @Override
    protected void configure() {
        LOGGER.info("Binding WebDriver to the thread-local provider");
        bind(WebDriver.class).toProvider(ThreadLocalWebDriverProvider.class);
    }
}
