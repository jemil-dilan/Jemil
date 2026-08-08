package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1;
import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SuspendAgencyUseCaseImpl implements SuspendAgencyUseCase {
    private final AgencyRepository agencyRepository;

    @Override
    public AgencyView1 execute(AgencyId agencyId) {
        Agency agency = agencyRepository.loadById(agencyId);
        agency.suspend();
        agencyRepository.save(agency);
        return agencyRepository.loadByIdAgencyView1(agencyId);
    }
}
