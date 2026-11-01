package module.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import module.config.providers.WebDriverProvider;
import module.config.providers.WebDriverWaitProvider;
import module.config.qualifiers.BaseUrl;
import module.config.qualifiers.Driver;
import module.config.qualifiers.DriverWait;

public final class WebDriverModule extends AbstractModule {

    @Override
    protected void configure() {
        // Agora o WebDriver padrão do projeto responde pelo qualificador @Driver
        bind(WebDriver.class).annotatedWith(Driver.class).toProvider(WebDriverProvider.class).in(Scopes.SINGLETON);
        bind(WebDriverWait.class).annotatedWith(DriverWait.class).toProvider(WebDriverWaitProvider.class);
        bind(String.class).annotatedWith(BaseUrl.class).toInstance("https://demoblaze.com");
    }
}