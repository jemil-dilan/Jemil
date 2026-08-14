package cm.jemil.agency.adapter.outbound.persistence.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@SuppressWarnings("JpaDataSourceORMInspection")
@Getter
@Setter
@NoArgsConstructor
@Entity
@FieldNameConstants
@Table(name = "t_routes")
@AllArgsConstructor
public class RouteJpa {
    @Id
    @Column(name = "c_id")
    private UUID id;

    @Column(name = "c_agency_id", nullable = false)
    private UUID agencyId;

    @Column(name = "c_departure", nullable = false)
    private UUID departureId;

    @Column(name = "c_arrival", nullable = false)
    private UUID arrivalId;

    @Column(name = "c_price", nullable = false)
    private double price;

    @Column(name = "c_total_seats", nullable = false)
    private int totalSeats;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "route_id")
    private List<ScheduleJpa> schedules = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RouteJpa routeJpa = (RouteJpa) o;
        return Objects.equals(id, routeJpa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
