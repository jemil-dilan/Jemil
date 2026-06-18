package cm.jemil.agency.domain.exception;

import cm.jemil.shared.exception.DomainException;

public class AgencyNotFoundException extends DomainException {

    public AgencyNotFoundException() {
        super(AgencyErrorCode.AGENCY_404_001.getCode(), AgencyErrorCode.AGENCY_404_001.getMessage());
    }

    public static AgencyNotFoundException forId(String id) {
        return new AgencyNotFoundException();
    }
}
