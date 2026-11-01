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

        bind(String.class).annotatedWith(BaseUrl.class).toInstance(baseUrl);
        bind(String.class).annotatedWith(CepEndpoint.class).toInstance(cepEndpoint);
        bind(RequestSpecification.class).toProvider(ViaCepRequestProvider.class);
    }

    private static Map<?, ?> loadConfiguration() {
        try (InputStream input = ViaCepModule.class.getClassLoader()
                .getResourceAsStream(CONFIG_RESOURCE)) {
            if (input == null) {
                throw new IllegalStateException("Recurso ausente: " + CONFIG_RESOURCE);
            }
            Object configuration = new Yaml(new SafeConstructor(new LoaderOptions())).load(input);
            return requiredMap(configuration, CONFIG_RESOURCE);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler " + CONFIG_RESOURCE, e);
        }
    }

    private static Map<?, ?> requiredMap(Object value, String key) {
        if (value instanceof Map<?, ?> map) {
            return map;
        }
        throw invalidConfiguration(key);
    }

    private static String requiredString(Object value, String key) {
        if (value instanceof String string && !string.isBlank()) {
            return string;
        }
        throw invalidConfiguration(key);
    }

    private static IllegalStateException invalidConfiguration(String key) {
        return new IllegalStateException("Configuração inválida: " + key);
    }
}