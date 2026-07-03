package cm.jemil.agency.domain.agency.views;

import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyStatus;
import cm.jemil.shared.utils.CreatedAt;
import cm.jemil.shared.utils.PhoneNumber;
import java.util.List;
import java.util.UUID;

public sealed interface AgencyView permits AgencyView.AgencyView1 {
    record AgencyView1(
            AgencyId id,
            String name,
            PhoneNumber phoneNumber,
            AgencyStatus status,
            List<BranchView> branches,
            String licenseNumber,
            double commissionRate,
            CreatedAt createdAt)
            implements AgencyView {}

    record BranchView(UUID id, String name, String address, UUID cityId, String cityName, boolean active) {}
}
