package module.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.google.inject.AbstractModule;

import module.config.providers.ViaCepRequestProvider;
import module.config.qualifiers.BaseUrl;
import module.config.qualifiers.CepEndpoint;

import io.restassured.specification.RequestSpecification;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

public final class ViaCepModule extends AbstractModule {
    private static final String CONFIG_RESOURCE = "endpoints.yml";

    private final Map<?, ?> configuration = loadConfiguration();

    /**
     * Creates a new instance of {@link ViaCepModule} and loads configuration from the YAML resource.
     */
    @Override
    protected void configure() {
        String baseUrl = requiredString(configuration.get("base-url"), "base-url");
        if (!baseUrl.startsWith("https://")) {
            throw invalidConfiguration("base-url");
        }

        Map<?, ?> endpoints = requiredMap(configuration.get("endpoints"), "endpoints");
        String cepEndpoint = requiredString(endpoints.get("consulta-cep"), "endpoints.consulta-cep");
        if (!cepEndpoint.startsWith("/") || !cepEndpoint.contains("{cep}")) {
            throw invalidConfiguration("endpoints.consulta-cep");
        }

        // Qualifiers distinguish the two String values injected by Guice.
        bind(String.class).annotatedWith(BaseUrl.class).toInstance(baseUrl);
        bind(String.class).annotatedWith(CepEndpoint.class).toInstance(cepEndpoint);
        bind(RequestSpecification.class).toProvider(ViaCepRequestProvider.class);
    }

    /**
     * Loads configuration from the YAML resource.
     *
     * @return the loaded configuration map
     */
    private static Map<?, ?> loadConfiguration() {
        try (InputStream input = ViaCepModule.class.getClassLoader()
                .getResourceAsStream(CONFIG_RESOURCE)) {
            if (input == null) {
                throw new IllegalStateException("Recurso ausente: " + CONFIG_RESOURCE);
            }
            // SafeConstructor parses YAML data without instantiating arbitrary Java types.
            Object configuration = new Yaml(new SafeConstructor(new LoaderOptions())).load(input);
            return requiredMap(configuration, CONFIG_RESOURCE);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler " + CONFIG_RESOURCE, e);
        }
    }

    /**
     * Returns the map if it is not null, otherwise throws an exception.
     *
     * @param value the value to check
     * @param key   the key for the error message
     * @return the map if it is not null
     */
    private static Map<?, ?> requiredMap(Object value, String key) {
        if (value instanceof Map<?, ?> map) {
            return map;
        }
        throw invalidConfiguration(key);
    }

    /**
     * Returns the string if it is not null or blank, otherwise throws an exception.
     *
     * @param value the value to check
     * @param key   the key for the error message
     * @return the string if it is not null or blank
    */
    private static String requiredString(Object value, String key) {
        if (value instanceof String string && !string.isBlank()) {
            return string;
        }
        throw invalidConfiguration(key);
    }

    /**
     * Returns an IllegalStateException for invalid configuration.
     *
     * @param key the key for the error message
     * @return the IllegalStateException
     */
    private static IllegalStateException invalidConfiguration(String key) {
        return new IllegalStateException("Configuração inválida: " + key);
    }
}