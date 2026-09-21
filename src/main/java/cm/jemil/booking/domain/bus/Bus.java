package cm.jemil.booking.domain.bus;

import cm.jemil.booking.domain.trip.BusId;
import cm.jemil.shared.utils.CreatedAt;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Domain entity representing a bus.
 */
@Getter
@Setter
public class Bus {

    private final BusId id;
    private final UUID agencyId;
    private final String label;
    private final String plate;
    private final int seatCount;
    private final String seatLayout;
    private final CreatedAt createdAt;

    private Bus(
            BusId id,
            UUID agencyId,
            String label,
            String plate,
            int seatCount,
            String seatLayout,
            CreatedAt createdAt) {
        this.id = Objects.requireNonNull(id, "Bus ID cannot be null");
        this.agencyId = Objects.requireNonNull(agencyId, "Agency ID cannot be null");
        this.label = Objects.requireNonNull(label, "Label cannot be null");
        this.plate = Objects.requireNonNull(plate, "Plate cannot be null");
        this.seatCount = seatCount;
        this.seatLayout = Objects.requireNonNull(seatLayout, "Seat layout cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");

        if (seatCount <= 0) {
            throw new IllegalArgumentException("Seat count must be positive");
        }
    }

    public static Bus create(
            UUID agencyId, String label, String plate, int seatCount, String seatLayout, CreatedAt createdAt) {
        return new Bus(BusId.generate(), agencyId, label, plate, seatCount, seatLayout, createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bus bus = (Bus) o;
        return Objects.equals(id, bus.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
