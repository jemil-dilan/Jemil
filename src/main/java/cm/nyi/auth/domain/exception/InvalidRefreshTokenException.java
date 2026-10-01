package cm.nyi.auth.domain.exception;

import cm.nyi.shared.exception.DomainException;

public class InvalidRefreshTokenException extends DomainException {

    public InvalidRefreshTokenException() {
        super(AuthErrorCode.AUTH_401_002);
    }
}
