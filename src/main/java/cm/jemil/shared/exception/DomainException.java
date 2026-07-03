package cm.jemil.shared.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
    private final String code;

    public DomainException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public DomainException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
