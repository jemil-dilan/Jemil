package cm.jemil.booking.domain.trip;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/** A trip to be materialised from an active schedule template for a given service date. */
public record TripToCreate(
        UUID id,
        UUID templateId,
        UUID agencyId,
        UUID routeId,
        UUID busId,
        OffsetDateTime departureAt,
        LocalDate serviceDate,
        int priceXaf,
        String travelClass,
        String status,
        int seatsTotal,
        int seatsSold) {}
