package cm.jemil.agency.adapter.outbound.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@FieldNameConstants
@Entity
@Table(name = "t_agency_branch")
@AllArgsConstructor
public class AgencyBranchJpa {
    @Id
    @Column(name = "c_id")
    private UUID id;

    @Column(name = "c_name")
    private String name;

    @Column(name = "c_address")
    private String address;

    @Column(name = "c_is_active")
    private boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_agency_id")
    private AgencyJpa agency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_city_id")
    private CityJpa city;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AgencyBranchJpa that = (AgencyBranchJpa) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
