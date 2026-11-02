package module.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.inject.AbstractModule;

import module.config.qualifiers.DefaultPdfPath;

public final class DefaultPdfPathModule extends AbstractModule {
    private static final String CONFIG_RESOURCE = "config.properties";
    private static final String PDF_PATH_PROPERTY = "api.test.pdf.path";
    private static final String FALLBACK_PATH = "target/api-test-logs.pdf";

    @Override
    protected void configure() {
        Properties properties = new Properties();
        try (InputStream input = DefaultPdfPathModule.class.getClassLoader()
                .getResourceAsStream(CONFIG_RESOURCE)) {
            if (input == null) {
                throw new IllegalStateException("Recurso ausente: " + CONFIG_RESOURCE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler " + CONFIG_RESOURCE, e);
        }

        String path = properties.getProperty(PDF_PATH_PROPERTY, FALLBACK_PATH);
        if (path.isBlank()) {
            throw new IllegalStateException("Configuração inválida: " + PDF_PATH_PROPERTY);
        }
        bind(String.class).annotatedWith(DefaultPdfPath.class).toInstance(path);
    }
}
