package cm.jemil.agency.domain.exception;

import cm.jemil.shared.exception.DomainException;

public class BranchNotFoundException extends DomainException {

    public BranchNotFoundException() {
        super(AgencyErrorCode.BRANCH_404_001);
    }
}
