package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAgencyByIdService implements GetAgencyByIdUseCase {
    private final AgencyRepository agencyRepository;

    @Override
    public Optional<Agency> execute(AgencyId id) {
        return agencyRepository.findById(id);
    }
}
