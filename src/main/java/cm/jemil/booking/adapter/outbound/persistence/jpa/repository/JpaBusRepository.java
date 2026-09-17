package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.BusJpa;
import cm.jemil.booking.adapter.outbound.persistence.jpa.repository.mapper.BusJpaMapper;
import cm.jemil.booking.domain.bus.Bus;
import cm.jemil.booking.domain.bus.BusId;
import cm.jemil.booking.domain.bus.BusRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaBusRepository implements BusRepository {

    private final BusSpringRepository busSpringRepository;
    private final BusJpaMapper jpaMapper;

    @Override
    public Bus save(Bus bus) {
        BusJpa jpa = jpaMapper.toJpa(bus);
        BusJpa saved = busSpringRepository.save(jpa);
        return jpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Bus> findById(BusId id) {
        return busSpringRepository.findById(id.value()).map(jpaMapper::toDomain);
    }

    @Override
    public List<Bus> findByAgencyId(UUID agencyId) {
        return busSpringRepository.findByAgencyId(agencyId).stream()
                .map(jpaMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Bus> findByUuid(UUID id) {
        return busSpringRepository.findById(id).map(jpaMapper::toDomain);
    }
}
