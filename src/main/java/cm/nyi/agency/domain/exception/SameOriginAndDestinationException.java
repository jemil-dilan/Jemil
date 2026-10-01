package cm.nyi.agency.domain.exception;

import cm.nyi.shared.exception.DomainException;

public class SameOriginAndDestinationException extends DomainException {

    public SameOriginAndDestinationException() {
        super(AgencyErrorCode.AGENCY_400_012);
    }
}
