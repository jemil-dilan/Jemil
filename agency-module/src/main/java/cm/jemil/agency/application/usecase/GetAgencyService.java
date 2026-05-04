package cm.jemil.agency.application.usecase;

import cm.jemil.agency.domain.model.Agency;
import cm.jemil.agency.domain.model.AgencyId;
import cm.jemil.agency.domain.port.in.GetAgencyUseCase;
import cm.jemil.agency.domain.port.out.AgencyRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application Service : consultation des agences.
 * Lecture seule — transaction en readOnly pour optimiser les performances.
 */
@Service
@Transactional(readOnly = true)
public class GetAgencyService implements GetAgencyUseCase {

    private final AgencyRepository agencyRepository;

    public GetAgencyService(AgencyRepository agencyRepository) {
        this.agencyRepository = agencyRepository;
    }

    @Override
    public Optional<Agency> findById(AgencyId agencyId) {
        return agencyRepository.findById(agencyId);
    }

    @Override
    public List<Agency> findAllActive() {
        return agencyRepository.findAllActive();
    }

    @Override
    public List<Agency> findByCity(String city) {
        return agencyRepository.findByCity(city);
    }
}
