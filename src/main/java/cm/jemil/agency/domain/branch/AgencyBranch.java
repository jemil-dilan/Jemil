package cm.jemil.agency.domain.branch;

import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.city.CityId;
import cm.jemil.shared.utils.CreatedAt;
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
        return new AgencyBranch(BranchId.generate(), agencyId, name, address, true, cityId, new CreatedAt());
    }

    public UUID id() {
        return id.value();
    }
}
