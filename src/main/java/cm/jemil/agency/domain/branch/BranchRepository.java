package cm.jemil.agency.domain.branch;

import cm.jemil.agency.domain.agency.AgencyId;
import java.util.List;
import java.util.Optional;

public interface BranchRepository {
    void save(AgencyBranch branch, AgencyId agencyId);

    Optional<AgencyBranch> findById(BranchId branchId);

    List<AgencyBranch> findAllByAgencyId(AgencyId agencyId);

    void delete(BranchId branchId);

    boolean existsById(BranchId branchId);
}
