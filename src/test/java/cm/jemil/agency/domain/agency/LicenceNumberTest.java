package cm.jemil.agency.domain.agency;

import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LicenceNumberTest {

    @Test
    void shouldCreateLicenceNumberWithValidValue() {
        LicenceNumber licence = new LicenceNumber("LIC-12345");
        assertEquals("LIC-12345", licence.value());
    }

    @Test
    void shouldRejectNullLicenceNumber() {
        DomainException exception = assertThrows(DomainException.class, 
                () -> new LicenceNumber(null));
        assertEquals(AgencyErrorCode.AGENCY_400_011.getCode(), exception.getCode());
        assertEquals("License number is required", exception.getMessage());
    }

    @Test
    void shouldRejectBlankLicenceNumber() {
        DomainException exception = assertThrows(DomainException.class, 
                () -> new LicenceNumber("   "));
        assertEquals(AgencyErrorCode.AGENCY_400_011.getCode(), exception.getCode());
        assertEquals("License number cannot be blank", exception.getMessage());
    }

    @Test
    void shouldRejectEmptyLicenceNumber() {
        DomainException exception = assertThrows(DomainException.class, 
                () -> new LicenceNumber(""));
        assertEquals(AgencyErrorCode.AGENCY_400_011.getCode(), exception.getCode());
        assertEquals("License number cannot be blank", exception.getMessage());
    }
}
