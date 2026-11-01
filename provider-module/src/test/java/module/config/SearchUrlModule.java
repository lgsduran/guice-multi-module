package module.config;

import com.google.inject.AbstractModule;

import module.config.qualifiers.BingUrl;
import module.config.qualifiers.GoogleUrl;

public final class SearchUrlModule extends AbstractModule {
    private static final String GOOGLE_URL = "https://www.google.com";
    private static final String BING_URL = "https://www.bing.com";

    @Override
    protected void configure() {
        bind(String.class).annotatedWith(GoogleUrl.class).toInstance(GOOGLE_URL);
        bind(String.class).annotatedWith(BingUrl.class).toInstance(BING_URL);
    }
}
