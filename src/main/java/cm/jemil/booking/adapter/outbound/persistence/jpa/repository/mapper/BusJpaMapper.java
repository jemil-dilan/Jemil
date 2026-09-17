package cm.jemil.booking.adapter.outbound.persistence.jpa.repository.mapper;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.BusJpa;
import cm.jemil.booking.domain.bus.Bus;
import cm.jemil.shared.utils.CreatedAt;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Mapper between Bus domain entity and BusJpa entity.
 */
@Component
public class BusJpaMapper {

    public BusJpa toJpa(Bus domain) {
        Objects.requireNonNull(domain, "Bus domain entity cannot be null");

        BusJpa jpa = new BusJpa();
        jpa.setId(domain.getId().value());
        jpa.setAgencyId(domain.getAgencyId());
        jpa.setLabel(domain.getLabel());
        jpa.setPlate(domain.getPlate());
        jpa.setSeatCount(domain.getSeatCount());
        jpa.setSeatLayout(domain.getSeatLayout());
        return jpa;
    }

    public Bus toDomain(BusJpa jpa) {
        Objects.requireNonNull(jpa, "BusJpa entity cannot be null");

        return Bus.create(
                jpa.getAgencyId(),
                jpa.getLabel(),
                jpa.getPlate(),
                jpa.getSeatCount(),
                jpa.getSeatLayout(),
                CreatedAt.reconstitute(jpa.getCreatedAt().toLocalDateTime()));
    }

    public void fromDomain(BusJpa jpa, Bus domain) {
        Objects.requireNonNull(jpa, "BusJpa entity cannot be null");
        Objects.requireNonNull(domain, "Bus domain entity cannot be null");

        jpa.setLabel(domain.getLabel());
        jpa.setPlate(domain.getPlate());
        jpa.setSeatCount(domain.getSeatCount());
        jpa.setSeatLayout(domain.getSeatLayout());
    }
}
