package cm.jemil.shared.adapter.inbound.rest;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.shared.exception.DomainException;
import cm.jemil.shared.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleDomainExceptionWith400() {
        var ex = new DomainException(new TestErrorCode("TEST_400_001", "Bad request"));

        var response = handler.handleDomainException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "TEST_400_001");
    }

    @Test
    void shouldHandleDomainExceptionWith404() {
        var ex = new DomainException(new TestErrorCode("TEST_404_001", "Not found"));

        var response = handler.handleDomainException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("error", "TEST_404_001");
    }

    @Test
    void shouldHandleDomainExceptionWith409() {
        var ex = new DomainException(new TestErrorCode("BOOKING_409_001", "Seat unavailable"));

        var response = handler.handleDomainException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("error", "BOOKING_409_001");
    }

    @Test
    void shouldHandleAccessDenied() {
        var ex = new AccessDeniedException("Forbidden");

        var response = handler.handleAccessDenied(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).containsEntry("error", "ACCESS_DENIED");
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        var ex = new IllegalArgumentException("Phone number is required");

        var response = handler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "VALIDATION_ERROR");
        assertThat(response.getBody()).containsEntry("message", "Phone number is required");
    }

    @Test
    void shouldHandleUnexpectedException() {
        var ex = new RuntimeException("Unexpected error");

        var response = handler.handleUnexpected(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("error", "INTERNAL_ERROR");
    }

    private record TestErrorCode(String code, String message) implements ErrorCode {
        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
