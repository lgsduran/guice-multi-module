package module.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public final class WebDriverModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(WebDriver.class).toProvider(WebDriverProvider.class).in(Scopes.SINGLETON);
        bind(WebDriverWait.class).toProvider(WebDriverWaitProvider.class).in(Scopes.SINGLETON);
        bind(String.class).annotatedWith(BaseUrl.class).toInstance("https://demoblaze.com/");
    }

}
