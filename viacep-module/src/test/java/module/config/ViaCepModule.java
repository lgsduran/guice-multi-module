package module.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import module.config.providers.ViaCepRequestProvider;
import module.config.qualifiers.BaseUrl;

import io.restassured.specification.RequestSpecification;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

public final class ViaCepModule extends AbstractModule {
    public static final String CEP_ENDPOINT = "viacep.endpoint.cep";
    private static final String CONFIG_RESOURCE = "endpoints.yml";

    private final String baseUrl;
    private final String cepEndpoint;

    public ViaCepModule() {
        this(loadConfiguration());
    }

    public ViaCepModule(String baseUrl) {
        this(baseUrl, readCepEndpoint(loadConfiguration()));
    }

    private ViaCepModule(Map<?, ?> configuration) {
        this(readBaseUrl(configuration), readCepEndpoint(configuration));
    }

    private ViaCepModule(String baseUrl, String cepEndpoint) {
        this.baseUrl = baseUrl;
        this.cepEndpoint = cepEndpoint;
    }

    private static Map<?, ?> loadConfiguration() {
        InputStream resource = ViaCepModule.class.getClassLoader().getResourceAsStream(CONFIG_RESOURCE);
        if (resource == null) {
            throw new IllegalStateException("Recurso de configuração ausente: " + CONFIG_RESOURCE);
        }

        try (InputStream input = resource) {
            Object configuration = new Yaml(new SafeConstructor(new LoaderOptions())).load(input);
            if (configuration instanceof Map<?, ?> values) {
                return values;
            }
            throw new IllegalStateException("Configuração inválida em " + CONFIG_RESOURCE);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler " + CONFIG_RESOURCE, e);
        }
    }

    private static String readBaseUrl(Map<?, ?> configuration) {
        Object value = configuration.get("base-url");
        if (value instanceof String baseUrl && baseUrl.startsWith("https://")) {
            return baseUrl;
        }
        throw new IllegalStateException("Configuração inválida em " + CONFIG_RESOURCE + ": base-url");
    }

    private static String readCepEndpoint(Map<?, ?> configuration) {
        Object endpointsValue = configuration.get("endpoints");
        if (endpointsValue instanceof Map<?, ?> endpoints
                && endpoints.get("consulta-cep") instanceof String endpoint
                && endpoint.startsWith("/")
                && endpoint.contains("{cep}")) {
            return endpoint;
        }
        throw new IllegalStateException(
                "Configuração inválida em " + CONFIG_RESOURCE + ": endpoints.consulta-cep");
    }

    @Override
    protected void configure() {
        bind(String.class).annotatedWith(BaseUrl.class).toInstance(baseUrl);
        bind(String.class).annotatedWith(Names.named(CEP_ENDPOINT)).toInstance(cepEndpoint);
        bind(RequestSpecification.class).toProvider(ViaCepRequestProvider.class);
    }
}