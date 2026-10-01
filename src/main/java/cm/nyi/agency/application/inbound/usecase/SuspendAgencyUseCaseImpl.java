package cm.nyi.agency.application.inbound.usecase;

import cm.nyi.agency.domain.agency.Agency;
import cm.nyi.agency.domain.agency.AgencyId;
import cm.nyi.agency.domain.agency.AgencyRepository;
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
