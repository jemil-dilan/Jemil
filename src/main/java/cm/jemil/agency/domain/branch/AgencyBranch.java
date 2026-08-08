package cm.jemil.agency.domain.branch;

import cm.jemil.agency.domain.city.CityId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AgencyBranch {
    private final BranchId id;
    private BranchName name;
    private BranchAddress address;
    private boolean active;
    private CityId cityId;

    public static AgencyBranch of(BranchName name, BranchAddress address, CityId cityId) {
        return new AgencyBranch(BranchId.generate(), name, address, true, cityId);
    }
}
