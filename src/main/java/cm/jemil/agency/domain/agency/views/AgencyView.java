package cm.jemil.agency.domain.agency.views;

import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyName;
import cm.jemil.agency.domain.agency.AgencyStatus;
import cm.jemil.agency.domain.agency.LicenceNumber;
import cm.jemil.agency.domain.branch.BranchAddress;
import cm.jemil.agency.domain.branch.BranchId;
import cm.jemil.agency.domain.branch.BranchName;
import cm.jemil.agency.domain.city.CityId;
import cm.jemil.shared.utils.CreatedAt;
import cm.jemil.shared.utils.PhoneNumber;
import java.util.List;

public sealed interface AgencyView permits AgencyView.AgencyView1 {
    record AgencyView1(
            AgencyId id,
            AgencyName name,
            PhoneNumber phoneNumber,
            AgencyStatus status,
            List<BranchView> branches,
            LicenceNumber licenseNumber,
            CreatedAt createdAt)
            implements AgencyView {}

    record BranchView(
            BranchId id,
            BranchName name,
            BranchAddress address,
            CityId cityId,
            AgencyId agencyId,
            boolean active,
            CreatedAt createdAt) {}
}
