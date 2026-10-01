package cm.nyi.agency.application.inbound.usecase;

import cm.nyi.agency.domain.agency.AgencyId;
import cm.nyi.agency.domain.agency.AgencyRepository;
import cm.nyi.agency.domain.branch.AgencyBranch;
import cm.nyi.agency.domain.branch.BranchAddress;
import cm.nyi.agency.domain.branch.BranchName;
import cm.nyi.agency.domain.branch.BranchRepository;
import cm.nyi.agency.domain.city.CityId;
import cm.nyi.agency.domain.city.CityRepository;
import cm.nyi.agency.domain.exception.AgencyNotFoundException;
import cm.nyi.agency.domain.exception.CityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddBranchUseCaseImpl implements AddBranchUseCase {
    private final AgencyRepository agencyRepository;
    private final BranchRepository branchRepository;
    private final CityRepository cityRepository;

    @Override
    public UUID execute(Command command) {
        if (!agencyRepository.existsById(command.getAgencyId())) {
            throw new AgencyNotFoundException();
        }

        if (!cityRepository.existsById(command.getCityId())) {
            throw new CityNotFoundException();
        }

        AgencyBranch branch = AgencyBranch.of(
                command.getAgencyId(), command.getBranchName(), command.getBranchAddress(), command.getCityId());
        branchRepository.save(branch);
        return branch.id();
    }

    public record Command(UUID agencyId, String name, String address, UUID cityId) {
        private AgencyId getAgencyId() {
            return new AgencyId(agencyId);
        }

        private BranchName getBranchName() {
            return new BranchName(name);
        }

        private BranchAddress getBranchAddress() {
            return new BranchAddress(address);
        }

        private CityId getCityId() {
            return new CityId(cityId);
        }
    }
}
