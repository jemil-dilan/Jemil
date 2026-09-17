package cm.jemil.booking.domain.trip;

import cm.jemil.booking.domain.bus.BusId;
import cm.jemil.shared.utils.CreatedAt;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Domain entity representing a trip.
 */
public class Trip {

    private final TripId id;
    private final ScheduleTemplateId templateId;
    private final BusId busId;
    private final java.util.UUID agencyId;
    private final java.util.UUID routeId;
    private final OffsetDateTime departureAt;
    private final LocalDate serviceDate;
    private final PriceXaf priceXaf;
    private final TravelClass travelClass;
    private TripStatus status;
    private final int seatsTotal;
    private int seatsSold;
    private final CreatedAt createdAt;

    private Trip(
            TripId id,
            ScheduleTemplateId templateId,
            BusId busId,
            java.util.UUID agencyId,
            java.util.UUID routeId,
            OffsetDateTime departureAt,
            LocalDate serviceDate,
            PriceXaf priceXaf,
            TravelClass travelClass,
            TripStatus status,
            int seatsTotal,
            int seatsSold,
            CreatedAt createdAt) {
        this.id = Objects.requireNonNull(id, "Trip ID cannot be null");
        this.templateId = templateId; // Can be null for one-offs
        this.busId = Objects.requireNonNull(busId, "Bus ID cannot be null");
        this.agencyId = Objects.requireNonNull(agencyId, "Agency ID cannot be null");
        this.routeId = Objects.requireNonNull(routeId, "Route ID cannot be null");
        this.departureAt = Objects.requireNonNull(departureAt, "Departure time cannot be null");
        this.serviceDate = Objects.requireNonNull(serviceDate, "Service date cannot be null");
        this.priceXaf = Objects.requireNonNull(priceXaf, "Price cannot be null");
        this.travelClass = Objects.requireNonNull(travelClass, "Travel class cannot be null");
        this.status = Objects.requireNonNull(status, "Trip status cannot be null");
        this.seatsTotal = seatsTotal;
        this.seatsSold = seatsSold;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
    }

    public static Trip create(
            ScheduleTemplateId templateId,
            BusId busId,
            java.util.UUID agencyId,
            java.util.UUID routeId,
            OffsetDateTime departureAt,
            LocalDate serviceDate,
            PriceXaf priceXaf,
            TravelClass travelClass,
            TripStatus status,
            int seatsTotal,
            int seatsSold,
            CreatedAt createdAt) {
        return new Trip(
                TripId.generate(),
                templateId,
                busId,
                agencyId,
                routeId,
                departureAt,
                serviceDate,
                priceXaf,
                travelClass,
                status,
                seatsTotal,
                seatsSold,
                createdAt);
    }

    public TripId getId() {
        return id;
    }

    public ScheduleTemplateId getTemplateId() {
        return templateId;
    }

    public BusId getBusId() {
        return busId;
    }

    public java.util.UUID getAgencyId() {
        return agencyId;
    }

    public java.util.UUID getRouteId() {
        return routeId;
    }

    public OffsetDateTime getDepartureAt() {
        return departureAt;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public PriceXaf getPriceXaf() {
        return priceXaf;
    }

    public int getPriceXafValue() {
        return priceXaf.amount();
    }

    public TravelClass getTravelClass() {
        return travelClass;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = Objects.requireNonNull(status, "Trip status cannot be null");
    }

    public int getSeatsTotal() {
        return seatsTotal;
    }

    public int getSeatsSold() {
        return seatsSold;
    }

    public void setSeatsSold(int seatsSold) {
        this.seatsSold = seatsSold;
    }

    public int getSeatsRemaining() {
        return seatsTotal - seatsSold;
    }

    public CreatedAt getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return Objects.equals(id, trip.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Trip{" + "id="
                + id + ", templateId="
                + templateId + ", busId="
                + busId + ", agencyId="
                + agencyId + ", routeId="
                + routeId + ", departureAt="
                + departureAt + ", serviceDate="
                + serviceDate + ", priceXaf="
                + priceXaf + ", travelClass="
                + travelClass + ", status="
                + status + ", seatsTotal="
                + seatsTotal + ", seatsSold="
                + seatsSold + '}';
    }
}
