package cm.jemil.agency.domain.exception;

import cm.jemil.shared.exception.DomainException;

public class DemoNotFoundException extends DomainException {

    public DemoNotFoundException() {
        super(AgencyErrorCode.AGENCY_404_002.getCode(), AgencyErrorCode.AGENCY_404_002.getMessage());
    }
}
