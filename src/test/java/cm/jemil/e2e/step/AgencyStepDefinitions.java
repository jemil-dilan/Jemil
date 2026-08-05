package cm.jemil.e2e.step;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.e2e.E2eHttpClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AgencyStepDefinitions {

    private static final Map<String, UUID> CITY_IDS = Map.of(
            "Douala", UUID.fromString("11111111-1111-1111-1111-111111111111"),
            "Yaounde", UUID.fromString("22222222-2222-2222-2222-222222222222"),
            "Yaoundé", UUID.fromString("22222222-2222-2222-2222-222222222222"));

    private final ScenarioContext context;
    private final E2eHttpClient httpClient;

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
        Response response = httpClient.post("/agency", context.getRequestBody());
        if (response.statusCode() == 201) {
            context.setCreatedAgencyId(UUID.fromString(response.jsonPath().getString("newId")));
        }
    }

    @When("I retrieve the agency by its id")
    public void iRetrieveTheAgencyById() {
        httpClient.get("/agency/{id}", context.getCreatedAgencyId());
    }

    @When("I list all agencies")
    public void iListAllAgencies() {
        httpClient.get("/agency");
    }

    @When("I search agencies in city {string}")
    public void iSearchAgenciesInCity(String city) {
        httpClient.get("/agency", Map.of("city", city));
    }

    @Then("the response contains an agency id")
    public void theResponseContainsAgencyId() {
        assertThat(context.getCreatedAgencyId()).isNotNull();
    }

    @Then("the agency name is {string}")
    public void theAgencyNameIs(String expectedName) {
        assertThat(httpClient.lastBody()).contains(expectedName);
    }

    @Then("the response contains at least {int} agency")
    public void theResponseContainsAtLeastNAgencies(int count) {
        Integer total = httpClient.getLastResponse().jsonPath().getInt("totalElements");
        assertThat(total).as("response should contain totalElements").isNotNull();
        assertThat(total).isGreaterThanOrEqualTo(count);
    }

    @Then("the response has validation errors")
    public void theResponseHasValidationErrors() {
        assertThat(httpClient.lastStatus()).isBetween(400, 499);
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
        Response response =
                httpClient.post("/agency/{id}/routes", context.getRequestBody(), context.getCreatedAgencyId());
        if (response.statusCode() == 201) {
            context.setCreatedRouteId(UUID.fromString(response.jsonPath().getString("id")));
        }
    }

    @Then("the response contains a route id")
    public void theResponseContainsARouteId() {
        assertThat(context.getCreatedRouteId()).isNotNull();
    }

    private UUID cityId(String city) {
        assertThat(CITY_IDS).containsKey(city);
        return CITY_IDS.get(city);
    }
}
