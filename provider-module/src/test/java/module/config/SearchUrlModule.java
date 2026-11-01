package module.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.inject.AbstractModule;

import module.config.qualifiers.BingUrl;
import module.config.qualifiers.GoogleUrl;

public final class SearchUrlModule extends AbstractModule {
    private static final String CONFIG_FILE = "config.properties";

    // Load search URLs from configuration and bind each one with its qualifier.
    @Override
    protected void configure() {
        Properties properties = new Properties();
        try (InputStream input = SearchUrlModule.class.getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Missing configuration resource: " + CONFIG_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read configuration resource: " + CONFIG_FILE, e);
        }

        bind(String.class).annotatedWith(GoogleUrl.class).toInstance(requiredUrl(properties, "google.url"));
        bind(String.class).annotatedWith(BingUrl.class).toInstance(requiredUrl(properties, "bing.url"));
    }

    private static String requiredUrl(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing configuration property: " + key);
        }
        return value.trim();
    }
}
