package cm.nyi.agency.domain.agency;

import java.util.UUID;

public record AgencyId(UUID value) {
    public static AgencyId generate() {
        return new AgencyId(UUID.randomUUID());
    }
}
