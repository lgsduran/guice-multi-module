package module.config.providers;

import module.config.qualifiers.BaseUrl;

import com.google.inject.Inject;
import com.google.inject.Provider;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class ViaCepRequestProvider implements Provider<RequestSpecification> {
    private final String baseUrl;

    @Inject
    public ViaCepRequestProvider(@BaseUrl String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public RequestSpecification get() {
        return RestAssured.given()
                .baseUri(baseUrl)
                .accept(ContentType.JSON);
    }
}
