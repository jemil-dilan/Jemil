package cm.jemil.agency.domain.model;

import java.util.Objects;
import java.util.UUID;

/** Value Object : identifiant d'une route. */
public record RouteId(UUID value) {

    public RouteId {
        Objects.requireNonNull(value, "RouteId ne peut pas être null");
    }

    public static RouteId generate() {
        return new RouteId(UUID.randomUUID());
    }

    public static RouteId from(String value) {
        return new RouteId(UUID.fromString(value));
    }

    public static RouteId from(UUID value) {
        return new RouteId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
