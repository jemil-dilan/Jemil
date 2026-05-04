package cm.jemil.agency.domain.exception;

/**
 * Exception domaine pour le module Agency.
 * Elle représente une violation d'une règle métier.
 * Elle NE doit PAS contenir de détails techniques (pas de stack trace HTTP).
 * C'est la couche infrastructure qui la traduit en réponse HTTP appropriée.
 */
public class AgencyDomainException extends RuntimeException {

    public AgencyDomainException(String message) {
        super(message);
    }

    public AgencyDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
