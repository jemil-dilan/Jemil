package cm.jemil.booking.domain.trip;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TripSearchView(
        UUID id,
        UUID agencyId,
        String agencyName,
        UUID routeId,
        UUID originCityId,
        UUID destinationCityId,
        OffsetDateTime departureAt,
        LocalDate serviceDate,
        int priceXaf,
        String travelClass,
        String status,
        int seatsTotal,
        int seatsRemaining) {}
