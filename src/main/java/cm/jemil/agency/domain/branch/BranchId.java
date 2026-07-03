package cm.jemil.agency.domain.branch;

import java.util.UUID;

public record BranchId(UUID value) {
    public static BranchId generate() {
        return new BranchId(UUID.randomUUID());
    }
}
