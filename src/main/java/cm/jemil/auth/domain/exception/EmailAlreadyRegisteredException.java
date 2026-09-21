package cm.jemil.auth.domain.exception;

import cm.jemil.shared.exception.DomainException;

public class EmailAlreadyRegisteredException extends DomainException {

    public EmailAlreadyRegisteredException() {
        super(AuthErrorCode.AUTH_409_001);
    }
}
