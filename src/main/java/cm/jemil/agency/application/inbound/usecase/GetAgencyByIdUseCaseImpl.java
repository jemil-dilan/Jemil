package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.views.AgencyView;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAgencyByIdUseCaseImpl implements GetAgencyByIdUseCase {
    private final AgencyRepository agencyRepository;

    @Override
    public AgencyView.AgencyView1 execute(UUID id) {
        return agencyRepository.loadByIdAgencyView1(new AgencyId(id));
    }
}
