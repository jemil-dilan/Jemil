package cm.nyi.agency.domain.exception;

import cm.nyi.shared.exception.DomainException;

public class BranchNotFoundException extends DomainException {

    public BranchNotFoundException() {
        super(AgencyErrorCode.BRANCH_404_001);
    }
}
