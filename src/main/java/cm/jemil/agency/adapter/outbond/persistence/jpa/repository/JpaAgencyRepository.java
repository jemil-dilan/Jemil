package cm.jemil.agency.adapter.outbond.persistence.jpa.repository;

import cm.jemil.agency.adapter.outbond.persistence.jpa.repository.mapper.AgencyPersistenceMapper;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaAgencyRepository implements AgencyRepository {

    private final AgencyJpaRepository agencyJpaRepository;
    private final AgencyPersistenceMapper mapper;

    @Override
    public void save(Agency agency) {
        agencyJpaRepository.save(mapper.toJpaEntity(agency));
    }

    @Override
    public Optional<Agency> findById(AgencyId id) {
        return agencyJpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Agency> findAll() {
        return agencyJpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }
}
