package cm.nyi.agency.domain.agency.views;

import cm.nyi.agency.domain.agency.AgencyId;
import cm.nyi.agency.domain.agency.AgencyName;
import cm.nyi.agency.domain.agency.AgencyStatus;
import cm.nyi.agency.domain.agency.LicenceNumber;
import cm.nyi.agency.domain.branch.BranchAddress;
import cm.nyi.agency.domain.branch.BranchId;
import cm.nyi.agency.domain.branch.BranchName;
import cm.nyi.agency.domain.city.CityId;
import cm.nyi.shared.utils.CreatedAt;
import cm.nyi.shared.utils.PhoneNumber;
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
