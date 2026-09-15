package cm.jemil.e2e.step;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.e2e.E2eHttpClient;
import cm.jemil.generated.booking.adapter.rest.inbound.dto.BookingHoldResponseDTO;
import cm.jemil.generated.booking.adapter.rest.inbound.dto.CreateBookingHoldDTO;
import cm.jemil.generated.booking.adapter.rest.inbound.dto.TripDTO;
import cm.jemil.generated.booking.adapter.rest.inbound.dto.TripSearchResponseDTO;
import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import org.springframework.jdbc.core.simple.JdbcClient;

public class BookingStep implements En {

    private static final UUID SEED_TRIP_ID = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

    private final E2eHttpClient httpClient;
    private TripSearchResponseDTO tripSearchResponseDTO;
    private BookingHoldResponseDTO bookingHoldResponseDTO;

    public BookingStep(JdbcClient jdbcClient, E2eHttpClient e2eHttpClient, ScenarioContext scenarioContext) {
        this.httpClient = e2eHttpClient;

        And("I assume that the trips with the following data are inside the database", (DataTable dataTable) -> {
            List<TripData> resultUnderTest = jdbcClient
                    .sql("""
                            SELECT id, agency_id, route_id, bus_id, price_xaf, travel_class, status, seats_total, seats_sold
                            FROM trips
                            """)
                    .query((rs, rowNum) -> TripData.builder()
                            .id(UUID.fromString(rs.getString("id")))
                            .agencyId(UUID.fromString(rs.getString("agency_id")))
                            .routeId(UUID.fromString(rs.getString("route_id")))
                            .busId(UUID.fromString(rs.getString("bus_id")))
                            .priceXaf(rs.getInt("price_xaf"))
                            .travelClass(rs.getString("travel_class"))
                            .status(rs.getString("status"))
                            .seatsTotal(rs.getInt("seats_total"))
                            .seatsSold(rs.getInt("seats_sold"))
                            .build())
                    .list();

            var list = dataTable.asMaps().stream()
                    .map(data -> TripData.builder()
                            .id(UUID.fromString(data.get("id")))
                            .agencyId(UUID.fromString(data.get("agencyId")))
                            .routeId(UUID.fromString(data.get("routeId")))
                            .busId(UUID.fromString(data.get("busId")))
                            .priceXaf(Integer.parseInt(data.get("priceXaf")))
                            .travelClass(data.get("travelClass"))
                            .status(data.get("status"))
                            .seatsTotal(Integer.parseInt(data.get("seatsTotal")))
                            .seatsSold(Integer.parseInt(data.get("seatsSold")))
                            .build())
                    .toList();
            assertThat(resultUnderTest).containsAll(list);
        });

        When("I search trips with the following data", (DataTable dataTable) -> {
            final var map = dataTable.asMaps().getFirst();
            final var serviceDate = "seeded".equalsIgnoreCase(map.get("serviceDate"))
                    ? seededServiceDate(jdbcClient)
                    : LocalDate.parse(map.get("serviceDate"));

            this.tripSearchResponseDTO = given().header("Authorization", "Bearer " + httpClient.getAccessToken())
                    .queryParam("originCityId", UUID.fromString(map.get("originCityId")))
                    .queryParam("destinationCityId", UUID.fromString(map.get("destinationCityId")))
                    .queryParam("serviceDate", serviceDate.toString())
                    .when()
                    .get("/trips/search")
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(TripSearchResponseDTO.class);
        });

        And("I should see that trips that has been fetched with the following data", (DataTable dataTable) -> {
            final var map = dataTable.asMaps().getFirst();
            assertThat(this.tripSearchResponseDTO).isNotNull();
            assertThat(this.tripSearchResponseDTO.getTotalElements())
                    .isGreaterThanOrEqualTo(Integer.parseInt(map.get("count")));
        });

        And("I should see that the following trips have been fetched", (DataTable dataTable) -> {
            assertThat(this.tripSearchResponseDTO).isNotNull();
            List<TripDTO> content = this.tripSearchResponseDTO.getContent();
            dataTable.asMaps().forEach(expected -> {
                boolean match = content.stream()
                        .anyMatch(trip -> UUID.fromString(expected.get("id")).equals(trip.getId())
                                && expected.get("status").equals(trip.getStatus()));
                assertThat(match)
                        .as(
                                "trip %s with status %s should be amongst the fetched ones",
                                expected.get("id"), expected.get("status"))
                        .isTrue();
            });
        });

        When("I place a booking hold with the following data", (DataTable dataTable) -> {
            var createDto = createBookingHoldDto(dataTable.asMaps().getFirst());
            this.bookingHoldResponseDTO = given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + httpClient.getAccessToken())
                    .body(createDto)
                    .when()
                    .post("/bookings")
                    .then()
                    .statusCode(201)
                    .extract()
                    .as(BookingHoldResponseDTO.class);
        });

        When("I try to place a booking hold with the following data", (DataTable dataTable) -> {
            var createDto = createBookingHoldDto(dataTable.asMaps().getFirst());
            Response response = given().contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + httpClient.getAccessToken())
                    .body(createDto)
                    .when()
                    .post("/bookings");
            httpClient.setLastResponse(response);
        });

        And("the hold response contains a booking reference for the following data", (DataTable dataTable) -> {
            final var map = dataTable.asMaps().getFirst();
            assertThat(this.bookingHoldResponseDTO).isNotNull();
            assertThat(this.bookingHoldResponseDTO.getRef()).startsWith("JML-");
            assertThat(this.bookingHoldResponseDTO.getSeatNos()).contains(Integer.parseInt(map.get("seatNos")));
        });
    }

    private LocalDate seededServiceDate(JdbcClient jdbcClient) {
        var serviceDate = jdbcClient
                .sql("SELECT service_date FROM trips WHERE id = ?")
                .param(SEED_TRIP_ID)
                .query((rs, rowNum) -> rs.getObject("service_date", LocalDate.class))
                .optional();
        assertThat(serviceDate).as("seeded trip should exist before searching").isPresent();
        return serviceDate.orElseThrow();
    }

    private CreateBookingHoldDTO createBookingHoldDto(Map<String, String> map) {
        return new CreateBookingHoldDTO()
                .tripId(UUID.fromString(map.get("tripId")))
                .seatNos(parseSeatNos(map.get("seatNos")))
                .passengerName(map.get("passengerName"))
                .passengerMsisdn(map.get("passengerMsisdn"));
    }

    private List<Integer> parseSeatNos(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .toList();
    }

    @Builder
    record TripData(
            UUID id,
            UUID agencyId,
            UUID routeId,
            UUID busId,
            int priceXaf,
            String travelClass,
            String status,
            int seatsTotal,
            int seatsSold) {}
}
