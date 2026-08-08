package cm.jemil.shared.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cm.jemil.shared.exception.DomainException;
import cm.jemil.shared.exception.SharedErrorCode;
import org.junit.jupiter.api.Test;

class PhoneNumberTest {

    @Test
    void shouldCreatePhoneNumber() {
        var phone = new PhoneNumber("237", "653492410");
        assertThat(phone.countryCode()).isEqualTo("237");
        assertThat(phone.number()).isEqualTo("653492410");
        assertThat(phone.fullNumber()).isEqualTo("+237653492410");
    }

    @Test
    void shouldRejectBlankCountryCode() {
        assertThatThrownBy(() -> new PhoneNumber(" ", "653492410"))
                .isInstanceOf(DomainException.class)
                .extracting("code")
                .isEqualTo(SharedErrorCode.PHONE_400_001.getCode());
    }

    @Test
    void shouldRejectBlankNumber() {
        assertThatThrownBy(() -> new PhoneNumber("237", " "))
                .isInstanceOf(DomainException.class)
                .extracting("code")
                .isEqualTo(SharedErrorCode.PHONE_400_001.getCode());
    }
}
