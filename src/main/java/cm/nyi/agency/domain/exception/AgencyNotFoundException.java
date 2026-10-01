package cm.nyi.agency.domain.exception;

import cm.nyi.shared.exception.DomainException;

public class AgencyNotFoundException extends DomainException {

    public AgencyNotFoundException() {
        super(AgencyErrorCode.AGENCY_404_001);
    }
}
