package cm.jemil.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.e2e.E2eHttpClient.LastRequestResponse;
import cm.jemil.shared.config.jwt.JwtService;
import io.cucumber.java8.En;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

public class CommonSteps implements En {

    private final E2eHttpClient httpClient;
    private final JwtService jwtService;

    public CommonSteps(E2eHttpClient httpClient, JwtService jwtService) {
        this.httpClient = httpClient;
        this.jwtService = jwtService;

        Before(this.httpClient::configureRestAssured);

        Given(
                "I am connected as user id {string} with the roles {string}",
                (String userId, String commaSeparatedRoles) -> {
                    Set<String> roles = Arrays.stream(commaSeparatedRoles.split(","))
                            .map(String::trim)
                            .filter(role -> !role.isEmpty())
                            .collect(Collectors.toSet());
                    this.httpClient.setAccessToken(this.jwtService.generateAccessToken(userId, roles));
                });

        Given("I am not authenticated", this.httpClient::clearAccessToken);

        Then(
                "the last request failed with the http status {string} and error code {string}",
                (String httpStatus, String errorCode) -> assertThat(this.httpClient.lastRequestResponse())
                        .isEqualTo(new LastRequestResponse(
                                HttpStatus.valueOf(httpStatus.trim().toUpperCase()), errorCode)));

        Then(
                "the response status is {int}",
                (Integer expectedStatus) -> assertThat(this.httpClient.lastStatus()).isEqualTo(expectedStatus));
    }
}
