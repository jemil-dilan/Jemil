package cm.jemil.agency.infrastructure.web.controller;

import cm.jemil.agency.domain.exception.AgencyDomainException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduction des exceptions domaine en réponses HTTP.
 *
 * <p>C'est ici qu'on fait le pont entre les exceptions du domaine
 * (qui ne savent pas que HTTP existe) et les réponses HTTP.
 *
 * <p>AgencyDomainException → 400 Bad Request (règle métier violée)
 * Exception non gérée      → 500 Internal Server Error
 */
@RestControllerAdvice
public class AgencyExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(AgencyExceptionHandler.class);

    @ExceptionHandler(AgencyDomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(AgencyDomainException ex) {
        log.warn("Règle métier violée : {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "DOMAIN_RULE_VIOLATION");
        body.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Argument invalide : {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "INVALID_ARGUMENT");
        body.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(body);
    }
}
