package cm.jemil.auth.domain.exception;

import cm.jemil.shared.exception.DomainException;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super(AuthErrorCode.AUTH_401_001);
    }
}
