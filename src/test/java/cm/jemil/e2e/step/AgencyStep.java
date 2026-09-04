package cm.jemil.e2e.step;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.e2e.E2eHttpClient;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AddRouteDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AgencyDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AgencyPaginationDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreateAgencyDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.CreationResponseDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.PhoneNumberDTO;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.RouteSearchResponseDTO;
import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Builder;
import org.springframework.jdbc.core.simple.JdbcClient;

public class AgencyStep implements En {
    private final E2eHttpClient httpClient;
    private CreationResponseDTO creationResponse;
    private AgencyDTO agencyDTO;
    private AgencyPaginationDTO agencyPaginationDTO;
    private RouteSearchResponseDTO routeSearchResponseDTO;

    public AgencyStep(JdbcClient jdbcClient, E2eHttpClient e2eHttpClient, ScenarioContext scenarioContext) {
        this.httpClient = e2eHttpClient;

        And("I assume that the agency with the following data are not inside the database", (DataTable dataTable) -> {
            var resultUnderTest = jdbcClient
                    .sql("SELECT c_id FROM t_agency")
                    .params()
                    .query(String.class)
                    .list();

            var list = dataTable.asMaps().stream().map(data -> data.get("id")).toList();
            assertThat(resultUnderTest).isNotEmpty().doesNotContainAnyElementsOf(list);
        });

        When("I create an agency with the following data", (DataTable dataTable) -> {
            var map = dataTable.asMaps().getFirst();
            var createDto = new CreateAgencyDTO()
                    .name(map.get("name"))
                    .phoneNumber(new PhoneNumberDTO()
                            .countryCode(map.get("countryCode"))
                            .number(map.get("number")))
                    .licenseNumber(map.get("licenseNumber"));
            this.creationResponse = given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + httpClient.getAccessToken())
                    .body(createDto)
                    .when()
                    .post("/agency")
                    .then()
                    .statusCode(201)
                    .extract()
                    .as(CreationResponseDTO.class);
        });

        When("I try to create an agency with the following data", (DataTable dataTable) -> {
            var map = dataTable.asMaps().getFirst();
            var createDto = new CreateAgencyDTO()
                    .name(map.get("name"))
                    .phoneNumber(new PhoneNumberDTO()
                            .countryCode(map.get("countryCode"))
                            .number(map.get("number")))
                    .licenseNumber(map.get("licenseNumber"));
            Response response = given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + httpClient.getAccessToken())
                    .body(createDto)
                    .when()
                    .post("/agency");
            httpClient.setLastResponse(response);
        });

        When("I fetch an agency identified by {string}", (String agencyId) -> {
            this.agencyDTO = given().header("Authorization", "Bearer " + httpClient.getAccessToken())
                    .pathParam("agencyId", UUID.fromString(agencyId))
                    .when()
                    .get("/agency/{agencyId}")
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(AgencyDTO.class);
        });

        When("I add a route to an agency identified by {string}", (String agencyId, DataTable dataTable) -> {
            var map = dataTable.asMaps().getFirst();
            var routeDto = new AddRouteDTO()
                    .originCityId(UUID.fromString(map.get("originCityId")))
                    .destinationCityId(UUID.fromString(map.get("destinationCityId")))
                    .price(Double.parseDouble(map.get("price")))
                    .totalSeats(Integer.parseInt(map.get("totalSeats")));

            this.creationResponse = given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + httpClient.getAccessToken())
                    .pathParam("agencyId", UUID.fromString(agencyId))
                    .body(routeDto)
                    .when()
                    .post("/agency/{agencyId}/routes")
                    .then()
                    .statusCode(201)
                    .extract()
                    .as(CreationResponseDTO.class);
        });

        And("I should see that the agency has been created", () -> {
            assertThat(this.creationResponse)
                    .isNotNull()
                    .extracting(CreationResponseDTO::getNewId)
                    .isNotNull();
            var id = this.creationResponse.getNewId();
            var exchangeRate = jdbcClient
                    .sql("SELECT c_id FROM t_agency WHERE c_id = ?")
                    .param(id)
                    .query((rs, rowNum) -> rs.getString("c_id"))
                    .optional();
            assertThat(exchangeRate).isPresent();
        });

        And("I should see that the route has been created", () -> {
            assertThat(this.creationResponse)
                    .isNotNull()
                    .extracting(CreationResponseDTO::getNewId)
                    .isNotNull();
            var id = this.creationResponse.getNewId();
            var exchangeRate = jdbcClient
                    .sql("SELECT c_id FROM t_routes WHERE c_id = ?")
                    .param(id)
                    .query((rs, rowNum) -> rs.getString("c_id"))
                    .optional();
            assertThat(exchangeRate).isPresent();
        });

        And("I fetch all agencies with the following data", (DataTable dataTable) -> {
            final var map = dataTable.asMaps().getFirst();
            final var page = Integer.parseInt(map.get("page"));
            final var limit = Integer.parseInt(map.get("limit"));
            final var city = Optional.ofNullable(map.get("city")).orElse(null);
            RequestSpecification specification = given().header(
                            "Authorization", "Bearer " + httpClient.getAccessToken())
                    .queryParam("page", page)
                    .queryParam("limit", limit);

            if (city != null) {
                specification.queryParam("city", city);
            }
            this.agencyPaginationDTO = specification
                    .when()
                    .get("/agency")
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(AgencyPaginationDTO.class);
        });

        And("I assume that the agencies with the following data are inside the database", (DataTable dataTable) -> {
            List<AgencyData> resultUnderTest = jdbcClient
                    .sql("SELECT * FROM t_agency")
                    .query((rs, rowNum) -> AgencyData.builder()
                            .id(UUID.fromString(rs.getString("c_id")))
                            .name(rs.getString("c_name"))
                            .countryCode(rs.getString("c_country_code"))
                            .phoneNumber(rs.getString("c_phone_number"))
                            .licenceNumber(rs.getString("c_license_number"))
                            .status(rs.getString("c_status"))
                            .build())
                    .list();

            var list = dataTable.asMaps().stream()
                    .map(data -> AgencyData.builder()
                            .id(UUID.fromString(data.get("id")))
                            .name(data.get("name"))
                            .countryCode(data.get("countryCode"))
                            .phoneNumber(data.get("phoneNumber"))
                            .licenceNumber(data.get("licenseNumber"))
                            .status(data.get("status"))
                            .build())
                    .toList();
            assertThat(resultUnderTest).containsAll(list);
        });

        And("I assume that the route with the following data is inside the database", (DataTable dataTable) -> {
            List<RouteData> resultUnderTest = jdbcClient
                    .sql("SELECT c_id, c_agency_id, c_departure, c_arrival, c_price, c_total_seats FROM t_routes")
                    .query((rs, rowNum) -> RouteData.builder()
                            .id(UUID.fromString(rs.getString("c_id")))
                            .agencyId(UUID.fromString(rs.getString("c_agency_id")))
                            .departureId(UUID.fromString(rs.getString("c_departure")))
                            .arrivalId(UUID.fromString(rs.getString("c_arrival")))
                            .price(rs.getInt("c_price"))
                            .totalSeats(rs.getInt("c_total_seats"))
                            .build())
                    .list();

            var list = dataTable.asMaps().stream()
                    .map(data -> RouteData.builder()
                            .id(UUID.fromString(data.get("id")))
                            .agencyId(UUID.fromString(data.get("agencyId")))
                            .departureId(UUID.fromString(data.get("originCityId")))
                            .arrivalId(UUID.fromString(data.get("destinationCityId")))
                            .price(Integer.parseInt(data.get("price")))
                            .totalSeats(Integer.parseInt(data.get("totalSeats")))
                            .build())
                    .toList();
            assertThat(resultUnderTest).containsAll(list);
        });

        And("I assume that the schedule with the following data is inside the database", (DataTable dataTable) -> {
            List<ScheduleData> resultUnderTest = jdbcClient
                    .sql("SELECT c_id, route_id, c_total_seats, c_available_seats FROM schedules")
                    .query((rs, rowNum) -> ScheduleData.builder()
                            .id(UUID.fromString(rs.getString("c_id")))
                            .routeId(UUID.fromString(rs.getString("route_id")))
                            .totalSeats(rs.getInt("c_total_seats"))
                            .availableSeats(rs.getInt("c_available_seats"))
                            .build())
                    .list();

            var list = dataTable.asMaps().stream()
                    .map(data -> ScheduleData.builder()
                            .id(UUID.fromString(data.get("id")))
                            .routeId(UUID.fromString(data.get("routeId")))
                            .totalSeats(Integer.parseInt(data.get("totalSeats")))
                            .availableSeats(Integer.parseInt(data.get("availableSeats")))
                            .build())
                    .toList();
            assertThat(resultUnderTest).containsAll(list);
        });
        And("I should see that the agency with the following data have been fetched", (DataTable dataTable) -> {
            final var map = dataTable.asMaps().getFirst();
            assertThat(this.agencyDTO).returns(UUID.fromString(map.get("id")), AgencyDTO::getId);
        });
        And("I should see that the agencies below are amongst the fetch ones", (DataTable dataTable) -> {
            List<String> expectedIds = dataTable.asList();
            List<String> actualIds = this.agencyPaginationDTO.getContent().stream()
                    .map(AgencyDTO::getId)
                    .map(UUID::toString)
                    .limit(expectedIds.size())
                    .toList();
            assertThat(actualIds).containsAnyElementsOf(expectedIds);
        });

        And("I search routes with the following data", (DataTable dataTable) -> {
            final var map = dataTable.asMaps().getFirst();
            final var originCityId = UUID.fromString(map.get("originCityId"));
            final var destinationCityId = UUID.fromString(map.get("destinationCityId"));

            this.routeSearchResponseDTO = given().header("Authorization", "Bearer " + httpClient.getAccessToken())
                    .queryParam("originCityId", originCityId)
                    .queryParam("destinationCityId", destinationCityId)
                    .when()
                    .get("/routes/search")
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(RouteSearchResponseDTO.class);
        });
        And("I should see that routes that has been fetched with the following data", (DataTable dataTable) -> {
            Map<String, String> map = dataTable.asMaps().getFirst();
            assertThat(this.routeSearchResponseDTO.getTotalElements())
                    .isGreaterThanOrEqualTo(Integer.parseInt(map.get("count")));
        });
        And("I should see that no routes has been fetched", () -> {
            assertThat(this.routeSearchResponseDTO.getTotalElements()).isEqualTo(0);
        });
    }

    @Builder
    record AgencyData(
            UUID id,
            String name,
            String status,
            String countryCode,
            String phoneNumber,
            String licenceNumber,
            LocalDateTime createdAt) {}

    @Builder
    record RouteData(UUID id, UUID agencyId, UUID departureId, UUID arrivalId, int price, int totalSeats) {}

    @Builder
    record ScheduleData(UUID id, UUID routeId, int totalSeats, int availableSeats) {}
}
