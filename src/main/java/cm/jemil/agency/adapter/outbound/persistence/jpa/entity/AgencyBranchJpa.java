package cm.jemil.agency.adapter.outbound.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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

    @Column(name = "c_name", nullable = false)
    private String name;

    @Column(name = "c_address", nullable = false)
    private String address;

    @Column(name = "c_is_active")
    private boolean isActive;

    @Column(name = "c_agency_id")
    private UUID agencyId;

    @Column(name = "c_city_id")
    private UUID cityId;

    @Column(name = "c_created_at")
    private LocalDateTime createdAt;

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
