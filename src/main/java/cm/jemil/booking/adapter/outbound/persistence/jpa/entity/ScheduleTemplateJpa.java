package cm.jemil.booking.adapter.outbound.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "schedule_templates")
public class ScheduleTemplateJpa {

    @Id
    private UUID id;

    @Column(name = "route_id", nullable = false)
    private UUID routeId;

    @Column(name = "bus_id", nullable = false)
    private UUID busId;

    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    /** Bitmask: bit 0 = Monday … bit 6 = Sunday (ISO). */
    @Column(name = "days_of_week", nullable = false)
    private short daysOfWeek;

    @Column(name = "price_xaf", nullable = false)
    private int priceXaf;

    @Column(name = "travel_class", nullable = false, length = 32)
    private String travelClass;

    @Column(nullable = false)
    private boolean active;
}
