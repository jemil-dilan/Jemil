package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.branch.AgencyBranch;
import cm.jemil.agency.domain.branch.BranchRepository;
import cm.jemil.agency.domain.city.CityId;
import cm.jemil.agency.domain.city.CityRepository;
import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddBranchUseCaseImpl implements AddBranchUseCase {
    private final AgencyRepository agencyRepository;
    private final BranchRepository branchRepository;
    private final CityRepository cityRepository;

    @Override
    public AgencyBranch execute(UUID agencyId, String name, String address, UUID cityId) {
        AgencyId id = new AgencyId(agencyId);
        if (!agencyRepository.existsById(id)) {
            throw new DomainException(AgencyErrorCode.AGENCY_404_001);
        }

        if (cityRepository.findById(cityId).isEmpty()) {
            throw new DomainException(AgencyErrorCode.CITY_404_001);
        }

        AgencyBranch branch = AgencyBranch.of(name, address, new CityId(cityId));
        branchRepository.save(branch, id);
        return branch;
    }
}
