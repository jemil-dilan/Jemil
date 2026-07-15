package cm.jemil.auth.domain.exception;

import cm.jemil.shared.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    AUTH_401_001("AUTH_401_001", "Invalid email or password"),
    AUTH_401_002("AUTH_401_002", "Invalid or expired token"),
    AUTH_403_001("AUTH_403_001", "Insufficient permissions"),
    AUTH_409_001("AUTH_409_001", "Email already registered"),
    AUTH_400_001("AUTH_400_001", "Email is required"),
    AUTH_400_002("AUTH_400_002", "Password is required");

    private final String code;
    private final String message;
}
