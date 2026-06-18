package cm.jemil.agency.adapter.inbound.rest;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.agency.domain.exception.AgencyDomainException;
import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.agency.domain.exception.AgencyNotFoundException;
import cm.jemil.agency.domain.exception.DemoNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class AgencyExceptionHandlerTest {

    private final AgencyExceptionHandler handler = new AgencyExceptionHandler();

    @Test
    void shouldHandleAgencyNotFoundException() {
        var ex = AgencyNotFoundException.forId("123");

        var response = handler.handleNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsKey("error");
        assertThat(response.getBody()).containsEntry("error", AgencyErrorCode.AGENCY_404_001.getCode());
        assertThat(response.getBody().get("message")).isInstanceOf(String.class);
    }

    @Test
    void shouldHandleDemoNotFoundException() {
        var ex = new DemoNotFoundException();

        var response = handler.handleNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsKey("error");
        assertThat(response.getBody()).containsEntry("error", AgencyErrorCode.AGENCY_404_002.getCode());
        assertThat(response.getBody().get("message")).isInstanceOf(String.class);
    }

    @Test
    void shouldHandleDomainException() {
        var ex = new AgencyDomainException(AgencyErrorCode.AGENCY_400_001);

        var response = handler.handleDomainException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("error");
        assertThat(response.getBody()).containsEntry("error", AgencyErrorCode.AGENCY_400_001.getCode());
    }
}
