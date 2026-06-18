package cm.jemil.agency.adapter.outbond.persistence.jpa.entity;

import cm.jemil.agency.domain.agency.AgencyStatus;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "agencies")
@Getter
@Setter
public class AgencyJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgencyStatus status;

    private String city;
    private String district;
    private String countryCode;
    private String phoneNumber;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "agency_id")
    private List<RouteJpaEntity> routes;
}
