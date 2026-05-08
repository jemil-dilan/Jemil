package cm.jemil.agency.adpater.outbound.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

/** Entité JPA pour la persistance d'une route. */
@Entity
@Table(name = "routes")
public class RouteJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false)
    private AgencyJpaEntity agency;

    @Column(name = "origin", nullable = false, length = 100)
    private String origin;

    @Column(name = "destination", nullable = false, length = 100)
    private String destination;

    @Column(name = "total_seats", nullable = false)
    private int totalSeats;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected RouteJpaEntity() {}

    public RouteJpaEntity(UUID id, AgencyJpaEntity agency, String origin, String destination, int totalSeats) {
        this.id = id;
        this.agency = agency;
        this.origin = origin;
        this.destination = destination;
        this.totalSeats = totalSeats;
        this.active = true;
    }

    public UUID getId() {
        return id;
    }

    public AgencyJpaEntity getAgency() {
        return agency;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean a) {
        this.active = a;
    }
}
