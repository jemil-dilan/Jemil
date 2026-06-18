package cm.jemil.agency.domain.exception;

import cm.jemil.shared.exception.DomainException;

public class AgencyDomainException extends DomainException {

    public AgencyDomainException(AgencyErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getMessage());
    }

    public AgencyDomainException(String code, String message) {
        super(code, message);
    }
}
