package cm.jemil.agency.adapter.inbound.rest;

import cm.jemil.agency.domain.exception.AgencyDomainException;
import cm.jemil.agency.domain.exception.AgencyNotFoundException;
import cm.jemil.agency.domain.exception.DemoNotFoundException;
import cm.jemil.shared.exception.DomainException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "cm.jemil.agency.adapter.inbound.rest")
public class AgencyExceptionHandler {

    @ExceptionHandler({AgencyNotFoundException.class, DemoNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(DomainException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorBody(ex, HttpStatus.NOT_FOUND.value(), ex.getCode()));
    }

    @ExceptionHandler(AgencyDomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(AgencyDomainException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorBody(ex, HttpStatus.BAD_REQUEST.value(), ex.getCode()));
    }

    private Map<String, Object> buildErrorBody(RuntimeException ex, int status, String error) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status);
        body.put("error", error);
        body.put("message", ex.getMessage());
        return body;
    }
}
