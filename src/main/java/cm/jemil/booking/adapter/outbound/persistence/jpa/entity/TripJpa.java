package cm.jemil.booking.adapter.outbound.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "trips")
public class TripJpa {

    @Id
    private UUID id;

    @Column(name = "template_id")
    private UUID templateId;

    @Column(name = "agency_id", nullable = false)
    private UUID agencyId;

    @Column(name = "route_id", nullable = false)
    private UUID routeId;

    @Column(name = "bus_id", nullable = false)
    private UUID busId;

    @Column(name = "departure_at", nullable = false)
    private OffsetDateTime departureAt;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "price_xaf", nullable = false)
    private int priceXaf;

    @Column(name = "travel_class", nullable = false, length = 32)
    private String travelClass;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "seats_total", nullable = false)
    private int seatsTotal;

    @Column(name = "seats_sold", nullable = false)
    private int seatsSold;
}
