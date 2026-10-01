package cm.nyi.auth.domain.exception;

import cm.nyi.shared.exception.DomainException;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super(AuthErrorCode.AUTH_401_001);
    }
}
