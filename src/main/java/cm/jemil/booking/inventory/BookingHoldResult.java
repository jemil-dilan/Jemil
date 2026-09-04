package cm.jemil.booking.inventory;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record BookingHoldResult(
        UUID id, String ref, UUID tripId, OffsetDateTime holdExpiresAt, int amountXaf, List<Integer> seatNos) {}
