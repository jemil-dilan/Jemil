package cm.jemil.agency.adapter.outbond.persistence.jpa.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "routes")
@Getter
@Setter
public class RouteJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String departure;

    @Column(nullable = false)
    private String arrival;

    @Column(nullable = false)
    private double price;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "route_id")
    private List<ScheduleJpaEntity> schedules;
}
