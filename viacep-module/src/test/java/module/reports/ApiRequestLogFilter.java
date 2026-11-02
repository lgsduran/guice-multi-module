package module.reports;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public final class ApiRequestLogFilter implements Filter {
    @Override
    public Response filter(
            FilterableRequestSpecification request,
            FilterableResponseSpecification responseSpecification,
            FilterContext context) {
        ApiTestLog.record("Requisição: " + request.getMethod() + " " + request.getURI());

        Response response = context.next(request, responseSpecification);
        ApiTestLog.record("Resposta: HTTP " + response.statusCode());
        ApiTestLog.record("Corpo da resposta:");
        ApiTestLog.record(response.asPrettyString());
        return response;
    }
}
