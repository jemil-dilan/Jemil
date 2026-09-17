package cm.jemil.booking.domain.trip;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a schedule template ID.
 */
public record ScheduleTemplateId(UUID value) {

    public ScheduleTemplateId {
        Objects.requireNonNull(value, "Schedule template ID cannot be null");
    }

    public static ScheduleTemplateId generate() {
        return new ScheduleTemplateId(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleTemplateId that = (ScheduleTemplateId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
