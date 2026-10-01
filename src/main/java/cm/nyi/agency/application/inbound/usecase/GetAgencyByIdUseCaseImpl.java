package cm.nyi.agency.application.inbound.usecase;

import cm.nyi.agency.domain.agency.AgencyId;
import cm.nyi.agency.domain.agency.AgencyRepository;
import cm.nyi.agency.domain.agency.views.AgencyView;
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
