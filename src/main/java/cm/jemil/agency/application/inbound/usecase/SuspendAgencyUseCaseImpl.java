package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SuspendAgencyUseCaseImpl implements SuspendAgencyUseCase {
    private final AgencyRepository agencyRepository;

    @Override
    public void execute(UUID agencyId) {
        Agency agency = agencyRepository.loadById(new AgencyId(agencyId));
        agency.suspend();
        agencyRepository.update(agency);
    }
}
