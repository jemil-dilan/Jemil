package cm.jemil.agency.adapter.outbound.persistence.jpa.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
    private UUID id;

    @Column(nullable = false)
    private String departure;

    @Column(nullable = false)
    private String arrival;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int totalSeats;

    @ManyToMany(mappedBy = "routes")
    private Set<AgencyJpa> agencies = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "route_id")
    private List<ScheduleJpa> schedules;

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
