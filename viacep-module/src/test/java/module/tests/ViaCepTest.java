package module.tests;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import module.config.base.BaseTest;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("API ViaCEP")
@Feature("Consulta de CEP")
public class ViaCepTest extends BaseTest {
    @Test(description = "Deve retornar 200 OK e o endereço correto ao buscar um CEP válido")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Valida os campos de endereço retornados para um CEP válido.")
    public void deveRetornarCepValido() {
        Response response = consultarCep("01001000");

        response.then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .log().all()
            .body(matchesJsonSchemaInClasspath("schemas/viacep-response-schema.json"))
            .body("cep", equalTo("01001-000"))
            .body("logradouro", is("Praça da Sé"))
            .body("bairro", notNullValue())
            .body("localidade", equalTo("São Paulo"))
            .body("uf", equalTo("SP"));
    }

    @Test(description = "Deve retornar campo 'erro=true' quando o CEP possui formato válido mas não existe")
    @Severity(SeverityLevel.NORMAL)
    @Description("Valida o campo de erro quando o CEP não está cadastrado.")
    public void deveRetornarErroParaCepInexistente() {
        Response response = consultarCep("99999999");

        response.then().statusCode(200);
        Assert.assertTrue(response.jsonPath().getBoolean("erro"));
    }

    @Test(description = "Deve retornar status 400 Bad Request se o formato do CEP for totalmente inválido")
    @Severity(SeverityLevel.NORMAL)
    @Description("Valida a resposta HTTP 400 para um CEP com formato inválido.")
    public void deveRetornar400ParaFormatoInvalido() {
        Response response = consultarCep("123_INVALIDO");

        response.then().statusCode(400);
    }

    private Response consultarCep(String cep) {
        return request
                .pathParam("cep", cep)
                .get(cepEndpoint);
    }
}