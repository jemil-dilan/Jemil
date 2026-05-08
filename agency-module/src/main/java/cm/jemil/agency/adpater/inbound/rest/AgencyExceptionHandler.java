package cm.jemil.agency.adpater.inbound.rest;

import cm.jemil.agency.domain.exception.AgencyAlreadyExistsException;
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

    private static final Logger LOG = LoggerFactory.getLogger(AgencyExceptionHandler.class);

    @ExceptionHandler(AgencyDomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(AgencyDomainException ex) {
        LOG.warn("Règle métier violée : {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "DOMAIN_RULE_VIOLATION");
        body.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(AgencyAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleAgencyAlreadyExists(AgencyAlreadyExistsException ex) {
        LOG.warn("Conflit d'unicité agence : {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "AGENCY_ALREADY_EXISTS");
        body.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        LOG.warn("Argument invalide : {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "INVALID_ARGUMENT");
        body.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(body);
    }
}
