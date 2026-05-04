package cm.jemil.agency.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object représentant l'identifiant unique d'une agence.
 *
 * <p>En DDD, on n'utilise pas de simples UUID ou Long directement. On les enveloppe
 * dans des Value Objects typés. Avantages :
 * - Impossible de confondre un AgencyId avec un RouteId même si les deux sont des UUID
 * - La méthode generate() est le seul endroit où on crée un nouvel ID
 * - Immuable par construction
 */
public record AgencyId(UUID value) {

    public AgencyId {
        Objects.requireNonNull(value, "AgencyId ne peut pas être null");
    }

    /** Crée un nouvel identifiant unique. */
    public static AgencyId generate() {
        return new AgencyId(UUID.randomUUID());
    }

    /** Reconstruit un AgencyId depuis une String (ex: depuis la DB ou l'API). */
    public static AgencyId from(String value) {
        return new AgencyId(UUID.fromString(value));
    }

    /** Reconstruit un AgencyId depuis un UUID existant. */
    public static AgencyId from(UUID value) {
        return new AgencyId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
