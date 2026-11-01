package module.config.base;

import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import module.config.WebDriverModule;
import module.config.qualifiers.BaseUrl;
import module.config.qualifiers.Driver;
import module.config.extensions.ScreenshotExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ScreenshotExtension.class)
public abstract class BaseTest {
    private static final Logger LOGGER = Logger.getLogger(BaseTest.class.getName());

    protected Injector injector;

    @Inject
    @Driver
    public WebDriver driver;

    @Inject
    @BaseUrl
    private String baseUrl;

    @BeforeEach
    public void setUp() {
        LOGGER.info("Initializing Guice and WebDriver for the test");
        // Inicializa o injetor do Guice com o módulo configurado
        injector = Guice.createInjector(new WebDriverModule());

        // Injeta os campos anotados nesta classe (resolve o @BaseUrl)
        injector.injectMembers(this);

        // Obtém a instância do WebDriver mapeada com o qualificador @Driver
        driver = injector.getInstance(com.google.inject.Key.get(WebDriver.class, Driver.class));

        // Injeta as dependências na própria instância do caso de teste filho (como a
        // AboutUsPage)
        injector.injectMembers(this);

        // Navega para a URL base definida no módulo
        driver.navigate().to(baseUrl);
        LOGGER.info("Opened the configured base URL");
    }

    /**
     * Método de limpeza após cada teste.
     * Fecha o navegador e libera os recursos do WebDriver.
     */
    @AfterEach
    public void tearDown() {
        if (this.driver != null) {
            LOGGER.info("Closing the WebDriver after the test");
            this.driver.quit();
        }
    }
}