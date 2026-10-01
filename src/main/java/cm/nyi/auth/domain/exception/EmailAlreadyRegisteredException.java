package cm.nyi.auth.domain.exception;

import cm.nyi.shared.exception.DomainException;

public class EmailAlreadyRegisteredException extends DomainException {

    public EmailAlreadyRegisteredException() {
        super(AuthErrorCode.AUTH_409_001);
    }
}
