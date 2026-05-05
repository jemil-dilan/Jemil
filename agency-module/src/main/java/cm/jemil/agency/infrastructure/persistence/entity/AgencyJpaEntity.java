package cm.jemil.agency.infrastructure.persistence.entity;

import cm.jemil.agency.domain.model.AgencyStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entité JPA pour la persistance d'une agence.
 *
 * <p>IMPORTANT : Cette classe existe UNIQUEMENT dans l'infrastructure.
 * Le domaine ne la connaît PAS. Elle ne contient PAS de logique métier.
 * C'est juste un mapping entre la DB et Java.
 *
 * <p>Pourquoi séparer l'entité JPA de l'Aggregate domaine ?
 * - L'Aggregate domaine peut évoluer sans casser le schéma DB
 * - Le schéma DB peut évoluer sans casser le domaine
 * - On peut tester le domaine sans démarrer Spring / JPA
 */
@Entity
@Table(name = "agencies")
public class AgencyJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AgencyStatus status;

    @OneToMany(mappedBy = "agency", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RouteJpaEntity> routes = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    // JPA exige un constructeur sans argument
    protected AgencyJpaEntity() {}

    public AgencyJpaEntity(UUID id, String name, String city, String contactPhone, AgencyStatus status) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.contactPhone = contactPhone;
        this.status = status;
    }

    // ── Getters et Setters (JPA en a besoin) ──────────────────
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String phone) {
        this.contactPhone = phone;
    }

    public AgencyStatus getStatus() {
        return status;
    }

    public void setStatus(AgencyStatus status) {
        this.status = status;
    }

    public List<RouteJpaEntity> getRoutes() {
        return routes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
