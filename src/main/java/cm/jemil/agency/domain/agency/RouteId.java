package cm.jemil.agency.domain.agency;

import java.util.UUID;

public record RouteId(UUID value) {
    public static RouteId generate() {
        return new RouteId(UUID.randomUUID());
    }
}
