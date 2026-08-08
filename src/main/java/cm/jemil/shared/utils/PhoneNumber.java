package cm.jemil.shared.utils;

import cm.jemil.shared.exception.DomainException;
import cm.jemil.shared.exception.SharedErrorCode;

/**
 * Value object representing a phone number with country code.
 * Enforces business rules: both country code and number must be non-null and non-blank.
 */
public record PhoneNumber(String countryCode, String number) {
    public PhoneNumber {
        // Defense in depth: validate null and blank
        if (countryCode == null || number == null) {
            throw new DomainException(SharedErrorCode.PHONE_400_001, "Phone number and country code are required");
        }
        if (countryCode.isBlank() || number.isBlank()) {
            throw new DomainException(SharedErrorCode.PHONE_400_001, "Phone number and country code cannot be blank");
        }
    }

    public String fullNumber() {
        return "+" + countryCode + number;
    }
}

