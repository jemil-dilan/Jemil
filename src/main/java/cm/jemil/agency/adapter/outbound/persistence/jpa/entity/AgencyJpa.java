package cm.jemil.agency.adapter.outbound.persistence.jpa.entity;

import cm.jemil.agency.domain.agency.AgencyStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
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
@Table(name = "t_agency")
@AllArgsConstructor
public class AgencyJpa {
    @Id
    @Column(name = "c_id")
    private UUID id;

    @Column(name = "c_name", nullable = false)
    private String name;

    @Column(name = "c_country_code", nullable = false)
    private String phoneCountryCode;

    @Column(name = "c_phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "c_license_number", nullable = false)
    private String licenseNumber;

    @Column(name = "c_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AgencyStatus status;

    @Column(name = "c_created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "c_agency_id", nullable = false, insertable = false, updatable = false)
    private Set<AgencyBranchJpa> branches = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "c_agency_id", nullable = false, insertable = false, updatable = false)
    private Set<RouteJpa> routes = new HashSet<>();

    public void addBranch(AgencyBranchJpa branch) {
        branches.add(branch);
    }

    public void addRoute(RouteJpa route) {
        routes.add(route);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AgencyJpa that = (AgencyJpa) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
