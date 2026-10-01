package cm.nyi.agency.domain.branch;

import cm.nyi.agency.domain.agency.AgencyId;
import cm.nyi.agency.domain.agency.views.AgencyView.BranchView;
import java.util.List;

public interface BranchRepository {
    void save(AgencyBranch branch);

    BranchView loadById(BranchId branchId);

    List<BranchView> loadAllByAgencyId(AgencyId agencyId);

    void delete(BranchId branchId);

    boolean existsById(BranchId branchId);
}
