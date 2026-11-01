package module.config.providers;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import com.google.inject.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebDriverProvider implements Provider<WebDriver> {
    
    // Configuração agressiva e definitiva para desligar avisos do Selenium no console
    static {
        // Desliga o logger padrão do Java para o pacote de ferramentas do Selenium
        Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
        
        // Propriedades do sistema para silenciar os binários do ChromeDriver
        System.setProperty("webdriver.chrome.silentOutput", "true");
        System.setProperty("webdriver.chrome.args", "--disable-logging");
    }
    
    /**
     * Volatile WebDriver instance to ensure visibility across threads.
     */
    private volatile WebDriver driver;

    /**
     * Provides a singleton instance of WebDriver.
     * If the driver is not already created, it initializes a new ChromeDriver with specified options.
     *
     * @return a singleton instance of WebDriver
     */
    @Override
    public WebDriver get() {
        if (driver == null) {
            synchronized (this) {
                if (driver == null) {
                    driver = createChromeDriver();
                }
            }
        }
        return driver;
    }

    /**
     * Creates a new instance of ChromeDriver with specified options.
     *
     * @return a new instance of ChromeDriver
     */
    private WebDriver createChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
            "--headless",
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--window-size=1920,1080",
            "start-maximized"
        );
        return new ChromeDriver(options);
    }
}