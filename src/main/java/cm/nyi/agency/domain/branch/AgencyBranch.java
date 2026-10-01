package cm.nyi.agency.domain.branch;

import cm.nyi.agency.domain.agency.AgencyId;
import cm.nyi.agency.domain.city.CityId;
import cm.nyi.shared.utils.CreatedAt;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AgencyBranch {
    private final BranchId id;
    private final AgencyId agencyId;
    private BranchName name;
    private BranchAddress address;
    private boolean active;
    private CityId cityId;
    private CreatedAt createdAt;

    public static AgencyBranch of(AgencyId agencyId, BranchName name, BranchAddress address, CityId cityId) {
        return new AgencyBranch(BranchId.generate(), agencyId, name, address, true, cityId, CreatedAt.now());
    }

    public UUID id() {
        return id.value();
    }
}
