package cm.jemil.auth.domain.exception;

import cm.jemil.shared.exception.DomainException;

public class InvalidRefreshTokenException extends DomainException {

    public InvalidRefreshTokenException() {
        super(AuthErrorCode.AUTH_401_002);
    }
}
