package cm.jemil.e2e.step;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.e2e.E2eHttpClient;
import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import org.springframework.jdbc.core.simple.JdbcClient;

public class BookingStep implements En {

    private static final UUID SEED_TRIP_ID = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

    private final E2eHttpClient httpClient;
    private final ScenarioContext context;

    public BookingStep(JdbcClient jdbcClient, E2eHttpClient e2eHttpClient, ScenarioContext scenarioContext) {
        this.httpClient = e2eHttpClient;
        this.context = scenarioContext;

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
            httpClient.get(
                    "/trips/search",
                    Map.of(
                            "originCityId", map.get("originCityId"),
                            "destinationCityId", map.get("destinationCityId"),
                            "serviceDate", serviceDate.toString()));
        });

        And("I should see that trips that has been fetched with the following data", (DataTable dataTable) -> {
            final var map = dataTable.asMaps().getFirst();
            Response response = httpClient.getLastResponse();
            assertThat(response.statusCode()).isEqualTo(200);
            Integer total = response.jsonPath().getInt("totalElements");
            assertThat(total).as("response should contain totalElements").isNotNull();
            assertThat(total).isGreaterThanOrEqualTo(Integer.parseInt(map.get("count")));
        });

        And("I should see that the following trips have been fetched", (DataTable dataTable) -> {
            Response response = httpClient.getLastResponse();
            assertThat(response.statusCode()).isEqualTo(200);
            List<Map<String, Object>> content = response.jsonPath().getList("content");
            dataTable.asMaps().forEach(expected -> {
                boolean match = content.stream()
                        .anyMatch(trip -> String.valueOf(expected.get("id")).equals(String.valueOf(trip.get("id")))
                                && String.valueOf(expected.get("status")).equals(String.valueOf(trip.get("status"))));
                assertThat(match)
                        .as(
                                "trip %s with status %s should be amongst the fetched ones",
                                expected.get("id"), expected.get("status"))
                        .isTrue();
            });
        });

        When("I place a booking hold with the following data", (DataTable dataTable) -> {
            context.setRequestBody(bookingHoldJson(dataTable.asMaps().getFirst()));
            httpClient.post("/bookings", context.getRequestBody());
        });

        When("I try to place a booking hold with the following data", (DataTable dataTable) -> {
            context.setRequestBody(bookingHoldJson(dataTable.asMaps().getFirst()));
            httpClient.post("/bookings", context.getRequestBody());
        });

        And("the hold response contains a booking reference for the following data", (DataTable dataTable) -> {
            final var map = dataTable.asMaps().getFirst();
            Response response = httpClient.getLastResponse();
            assertThat(response.statusCode()).isEqualTo(201);
            assertThat(response.jsonPath().getString("ref")).startsWith("JML-");
            List<Integer> seatNos = response.jsonPath().getList("seatNos", Integer.class);
            assertThat(seatNos).contains(Integer.parseInt(map.get("seatNos")));
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

    private String bookingHoldJson(Map<String, String> map) {
        return """
                {
                    "tripId": "%s",
                    "seatNos": [%s],
                    "passengerName": "%s",
                    "passengerMsisdn": "%s"
                }
                """.formatted(
                        map.get("tripId"), map.get("seatNos"), map.get("passengerName"), map.get("passengerMsisdn"));
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
