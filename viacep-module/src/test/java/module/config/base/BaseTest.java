package module.config.base;

import org.junit.jupiter.api.BeforeEach;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

import module.config.ViaCepModule;

import io.restassured.specification.RequestSpecification;

public abstract class BaseTest {
    protected Injector injector;

    @Inject
    protected RequestSpecification request;

    @Inject
    @Named(ViaCepModule.CEP_ENDPOINT)
    protected String cepEndpoint;

    @BeforeEach
    protected void setUp() {
        injector = Guice.createInjector(new ViaCepModule());
        injector.injectMembers(this);
    }
}
