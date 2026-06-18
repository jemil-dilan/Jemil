package cm.jemil.agency.domain.agency;

import java.util.UUID;

public record ScheduleId(UUID value) {
    public static ScheduleId generate() {
        return new ScheduleId(UUID.randomUUID());
    }
}
