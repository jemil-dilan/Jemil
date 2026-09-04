package cm.jemil.e2e;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Shared RestAssured HTTP client for Cucumber e2e scenarios.
 * Holds the current bearer token and the last response for common assertions.
 */
@Component
@Scope("cucumber-glue")
public class E2eHttpClient {

    @LocalServerPort
    private int port;

    @Setter
    @Getter
    private String accessToken;

    @Getter
    @Setter
    private Response lastResponse;

    public void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.config = RestAssuredConfig.config()
                .encoderConfig(EncoderConfig.encoderConfig()
                        .defaultContentCharset("UTF-8")
                        .appendDefaultContentCharsetToContentTypeIfUndefined(false))
                .httpClient(HttpClientConfig.httpClientConfig().dontReuseHttpClientInstance());
    }

    public void clearAccessToken() {
        this.accessToken = null;
    }

    public Response get(String path, Object... pathParams) {
        return capture(readSpec().when().get(path, pathParams));
    }

    public Response get(String path, Map<String, ?> queryParams) {
        return capture(readSpec().queryParams(queryParams).when().get(path));
    }

    public Response post(String path, String jsonBody, Object... pathParams) {
        return capture(writeSpec().body(jsonBody == null ? "" : jsonBody).when().post(path, pathParams));
    }

    public Response put(String path, String jsonBody, Object... pathParams) {
        return capture(writeSpec().body(jsonBody == null ? "" : jsonBody).when().put(path, pathParams));
    }

    public Response delete(String path, Object... pathParams) {
        return capture(readSpec().when().delete(path, pathParams));
    }

    public int lastStatus() {
        return lastResponse == null ? -1 : lastResponse.statusCode();
    }

    public String lastBody() {
        return lastResponse == null ? "" : lastResponse.asString();
    }

    public Optional<String> lastErrorCode() {
        if (lastResponse == null) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(lastResponse.jsonPath().getString("error"));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public LastRequestResponse lastRequestResponse() {
        return new LastRequestResponse(
                HttpStatus.valueOf(lastStatus()), lastErrorCode().orElse(null));
    }

    private RequestSpecification readSpec() {
        return given().spec(baseSpec().build());
    }

    private RequestSpecification writeSpec() {
        return given().spec(baseSpec().setContentType(ContentType.JSON).build());
    }

    private RequestSpecBuilder baseSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder().setAccept(ContentType.JSON);
        if (accessToken != null && !accessToken.isBlank()) {
            builder.addHeader("Authorization", "Bearer " + accessToken);
        }
        return builder;
    }

    private Response capture(Response response) {
        this.lastResponse = response;
        return response;
    }

    public record LastRequestResponse(HttpStatus status, String errorCode) {}
}
