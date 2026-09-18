package cm.jemil.agency.domain.exception;

import cm.jemil.shared.exception.DomainException;

public class SameOriginAndDestinationException extends DomainException {

    public SameOriginAndDestinationException() {
        super(AgencyErrorCode.AGENCY_400_012);
    }
}
