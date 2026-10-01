package cm.nyi.shared.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SharedErrorCode implements ErrorCode {
    PHONE_400_001("PHONE_400_001", "Phone number is required");

    private final String code;
    private final String message;
}
