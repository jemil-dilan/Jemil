package cm.jemil.booking.domain.bus;

import cm.jemil.booking.domain.trip.BusId;
import cm.jemil.shared.utils.CreatedAt;
import java.util.Objects;

/**
 * Domain entity representing a bus.
 */
public class Bus {

    private final BusId id;
    private final java.util.UUID agencyId;
    private final String label;
    private final String plate;
    private final int seatCount;
    private final String seatLayout;
    private final CreatedAt createdAt;

    private Bus(
            BusId id,
            java.util.UUID agencyId,
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
            java.util.UUID agencyId,
            String label,
            String plate,
            int seatCount,
            String seatLayout,
            CreatedAt createdAt) {
        return new Bus(BusId.generate(), agencyId, label, plate, seatCount, seatLayout, createdAt);
    }

    public BusId getId() {
        return id;
    }

    public java.util.UUID getAgencyId() {
        return agencyId;
    }

    public String getLabel() {
        return label;
    }

    public String getPlate() {
        return plate;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public String getSeatLayout() {
        return seatLayout;
    }

    public CreatedAt getCreatedAt() {
        return createdAt;
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

    @Override
    public String toString() {
        return "Bus{" + "id="
                + id + ", agencyId="
                + agencyId + ", label='"
                + label + '\'' + ", plate='"
                + plate + '\'' + ", seatCount="
                + seatCount + ", seatLayout='"
                + seatLayout + '\'' + '}';
    }
}
