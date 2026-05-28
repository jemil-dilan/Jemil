package cm.jemil.auth.adapter.outbond.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@SuppressWarnings("JpaDataSourceORMInspection")
@FieldNameConstants
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "t_demo")
public class DemoJpa {

    @Id
    @Column(name = "c_id")
    private UUID id;

    @Column(name = "c_name")
    private String name;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DemoJpa demoJpa = (DemoJpa) o;
        return Objects.equals(id, demoJpa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
