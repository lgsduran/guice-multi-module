package module.config.base;

import org.testng.annotations.BeforeMethod;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import module.config.ViaCepModule;
import module.config.qualifiers.CepEndpoint;

import io.restassured.specification.RequestSpecification;

public abstract class BaseTest {
    protected Injector injector;

    @Inject
    protected RequestSpecification request;

    @Inject
    @CepEndpoint
    protected String cepEndpoint;

    @BeforeMethod
    protected void setUp() {
        injector = Guice.createInjector(new ViaCepModule());
        injector.injectMembers(this);
    }
}