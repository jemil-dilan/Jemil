package cm.jemil.agency.domain.branch;

import cm.jemil.agency.domain.agency.AgencyId;

public interface BranchRepository {
    void save(AgencyBranch branch, AgencyId agencyId);
}
