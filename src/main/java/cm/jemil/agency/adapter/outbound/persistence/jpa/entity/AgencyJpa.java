package cm.jemil.agency.adapter.outbound.persistence.jpa.entity;

import cm.jemil.agency.domain.agency.AgencyStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

    @Column(name = "c_name")
    private String name;

    @Column(name = "c_country_code")
    private String phoneCountryCode;

    @Column(name = "c_phone_number")
    private String phoneNumber;

    @Column(name = "c_licencse_number")
    private String licenseNumber;

    @Column(name = "c_commission_rate")
    private double commissionRate;

    @Column(name = "c_status")
    @Enumerated(EnumType.STRING)
    private AgencyStatus status;

    @Column(name = "c_created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "agency", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AgencyBranchJpa> branches = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "agency_routes",
            joinColumns = @JoinColumn(name = "agency_id"),
            inverseJoinColumns = @JoinColumn(name = "route_id"))
    private Set<RouteJpa> routes = new HashSet<>();

    public void addBranch(AgencyBranchJpa branch) {
        branches.add(branch);
        branch.setAgency(this);
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
