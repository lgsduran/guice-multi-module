package module.config.providers;

import module.config.qualifiers.BaseUrl;
import module.reports.ApiRequestLogFilter;

import com.google.inject.Inject;
import com.google.inject.Provider;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class ViaCepRequestProvider implements Provider<RequestSpecification> {
    private final String baseUrl;

    /**
     * Creates a new instance of {@link ViaCepRequestProvider}.
     *
     * @param baseUrl the base URL for the API requests
     */
    @Inject
    public ViaCepRequestProvider(@BaseUrl String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Returns a new instance of {@link RequestSpecification} configured with the base URL.
     *
     * @return the configured request specification
     */
    @Override
    public RequestSpecification get() {
        return RestAssured.given()
                .baseUri(baseUrl)
                .accept(ContentType.JSON)
                .filter(new ApiRequestLogFilter());
    }
}