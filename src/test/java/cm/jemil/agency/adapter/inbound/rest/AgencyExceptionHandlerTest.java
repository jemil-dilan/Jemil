package cm.jemil.agency.adapter.inbound.rest;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.adapter.inbound.rest.GlobalExceptionHandler;
import cm.jemil.shared.exception.DomainException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class AgencyExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleAgencyNotFound() {
        var ex = new DomainException(AgencyErrorCode.AGENCY_404_001);

        var response = handler.handleDomainException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsKey("error");
        assertThat(response.getBody()).containsEntry("error", AgencyErrorCode.AGENCY_404_001.getCode());
        assertThat(response.getBody().get("message")).isInstanceOf(String.class);
    }

    @Test
    void shouldHandleDemoNotFound() {
        var ex = new DomainException(AgencyErrorCode.AGENCY_404_002);

        var response = handler.handleDomainException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsKey("error");
        assertThat(response.getBody()).containsEntry("error", AgencyErrorCode.AGENCY_404_002.getCode());
        assertThat(response.getBody().get("message")).isInstanceOf(String.class);
    }

    @Test
    void shouldHandleDomainException() {
        var ex = new DomainException(AgencyErrorCode.AGENCY_400_001);

        var response = handler.handleDomainException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("error");
        assertThat(response.getBody()).containsEntry("error", AgencyErrorCode.AGENCY_400_001.getCode());
    }
}
