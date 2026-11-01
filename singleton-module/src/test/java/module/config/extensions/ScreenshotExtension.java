package module.config.extensions;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;

public class ScreenshotExtension implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        try {
            // 1. Pega a instância do teste que acabou de rodar e falhar
            Object instanciaDoTeste = context.getRequiredTestInstance();

            // 2. Busca o campo chamado 'driver' dentro daquela classe de teste
            Field campoDriver = instanciaDoTeste.getClass().getSuperclass().getDeclaredField("driver");
            campoDriver.setAccessible(true); // Permite ler o campo mesmo que ele seja protected

            // 3. Extrai o WebDriver ativo daquele campo
            WebDriver driverAtivo = (WebDriver) campoDriver.get(instanciaDoTeste);

            if (driverAtivo != null) {
                // 4. Captura o screenshot da tela em bytes
                byte[] screenshot = ((TakesScreenshot) driverAtivo).getScreenshotAs(OutputType.BYTES);

                // 5. Pega o nome do método de teste que falhou para dar nome à evidência
                String nomeDoTeste = context.getRequiredTestMethod().getName();

                // 6. Anexa a imagem diretamente no relatório final do Allure
                Allure.addAttachment("Falha no teste: " + nomeDoTeste, new ByteArrayInputStream(screenshot));
            }

        } catch (Exception e) {
            System.err.println("Não foi possível capturar a evidência: " + e.getMessage());
        }
    }
}