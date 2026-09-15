package cm.jemil.booking.domain.trip;

import java.util.List;
import java.util.UUID;

/** UC-P-02 seat map snapshot — layout from the bus, taken = HELD or SOLD. */
public record TripSeatMap(UUID tripId, int seatCount, String layout, List<Integer> taken) {}
