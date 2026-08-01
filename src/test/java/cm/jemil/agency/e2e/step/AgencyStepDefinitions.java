package cm.jemil.agency.e2e.step;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
public class AgencyStepDefinitions {

    private static final Pattern UUID_FIELD = Pattern.compile("\"(?:newId|id)\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern TOTAL_ELEMENTS = Pattern.compile("\"totalElements\"\\s*:\\s*(\\d+)");
    private static final Map<String, UUID> CITY_IDS = Map.of(
            "Douala", UUID.fromString("11111111-1111-1111-1111-111111111111"),
            "Yaounde", UUID.fromString("22222222-2222-2222-2222-222222222222"),
            "Yaoundé", UUID.fromString("22222222-2222-2222-2222-222222222222"));

    private final ScenarioContext context;
    private final TestJwtHelper jwtHelper;
    private final TestRestTemplate restTemplate;

    @Given("a valid agency payload with name {string} and phone {string}")
    public void aValidAgencyPayload(String name, String phone) {
        var countryCode = phone.substring(0, 3);
        var number = phone.substring(3);
        var license = "LIC-" + UUID.randomUUID().toString().substring(0, 8);
        context.setRequestBody("""
                {
                    "name": "%s",
                    "phoneNumber": { "countryCode": "%s", "number": "%s" },
                    "licenseNumber": "%s",
                    "commissionRate": 5.0
                }
                """.formatted(name, countryCode, number, license));
    }

    @Given("an invalid agency payload with phone {string}")
    public void anInvalidAgencyPayload(String phone) {
        // Domain requires a non-blank phone number; empty number triggers 400.
        context.setRequestBody("""
                {
                    "name": "Invalid Phone %s",
                    "phoneNumber": { "countryCode": "237", "number": "" },
                    "licenseNumber": "LIC-INVALID-%s",
                    "commissionRate": 5.0
                }
                """.formatted(phone, UUID.randomUUID().toString().substring(0, 8)));
    }

    @When("I register the agency as an admin")
    public void iRegisterTheAgencyAsAdmin() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/agency", HttpMethod.POST, authenticatedJsonRequest(context.getRequestBody()), String.class);

        capture(response);
        if (response.getStatusCode().value() == 201) {
            context.setCreatedAgencyId(extractUuid(response.getBody()));
        }
    }

    @When("I retrieve the agency by its id")
    public void iRetrieveTheAgencyById() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/agency/{id}", String.class, context.getCreatedAgencyId());
        capture(response);
    }

    @When("I list all agencies")
    public void iListAllAgencies() {
        ResponseEntity<String> response = restTemplate.getForEntity("/agency", String.class);
        capture(response);
    }

    @When("I search agencies in city {string}")
    public void iSearchAgenciesInCity(String city) {
        URI uri = UriComponentsBuilder.fromPath("/agency")
                .queryParam("city", city)
                .build()
                .encode()
                .toUri();
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        capture(response);
    }

    @Then("the response status is {int}")
    public void theResponseStatusIs(int expectedStatus) {
        assertThat(context.getResponseStatus()).isEqualTo(expectedStatus);
    }

    @Then("the response contains an agency id")
    public void theResponseContainsAgencyId() {
        assertThat(context.getCreatedAgencyId()).isNotNull();
    }

    @Then("the agency name is {string}")
    public void theAgencyNameIs(String expectedName) {
        assertThat(context.getResponseBody()).contains(expectedName);
    }

    @Then("the response contains at least {int} agency")
    public void theResponseContainsAtLeastNAgencies(int count) {
        assertThat(totalElements(context.getResponseBody())).isGreaterThanOrEqualTo(count);
    }

    @Then("the response has validation errors")
    public void theResponseHasValidationErrors() {
        assertThat(context.getResponseStatus()).isBetween(400, 499);
    }

    @Given("I add a route from {string} to {string} with price {double} and {int} seats")
    public void iAddARoute(String origin, String destination, double price, int seats) {
        context.setRequestBody("""
                {
                    "originCityId": "%s",
                    "destinationCityId": "%s",
                    "price": %.0f,
                    "totalSeats": %d
                }
                """.formatted(cityId(origin), cityId(destination), price, seats));
    }

    @When("I add the route to the agency")
    public void iAddTheRouteToTheAgency() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/agency/{id}/routes",
                HttpMethod.POST,
                authenticatedJsonRequest(context.getRequestBody()),
                String.class,
                context.getCreatedAgencyId());

        capture(response);
        if (response.getStatusCode().value() == 201) {
            context.setCreatedRouteId(extractUuid(response.getBody()));
        }
    }

    @Then("the response contains a route id")
    public void theResponseContainsARouteId() {
        assertThat(context.getCreatedRouteId()).isNotNull();
    }

    private HttpEntity<String> authenticatedJsonRequest(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtHelper.adminToken());
        return new HttpEntity<>(body, headers);
    }

    private void capture(ResponseEntity<String> response) {
        context.setResponseStatus(response.getStatusCode().value());
        context.setResponseBody(response.getBody() == null ? "" : response.getBody());
    }

    private UUID extractUuid(String body) {
        var matcher = UUID_FIELD.matcher(body == null ? "" : body);
        assertThat(matcher.find()).as("response should contain a UUID id field").isTrue();
        return UUID.fromString(matcher.group(1));
    }

    private int totalElements(String body) {
        var matcher = TOTAL_ELEMENTS.matcher(body == null ? "" : body);
        assertThat(matcher.find()).as("response should contain totalElements").isTrue();
        return Integer.parseInt(matcher.group(1));
    }

    private UUID cityId(String city) {
        assertThat(CITY_IDS).containsKey(city);
        return CITY_IDS.get(city);
    }
}
