package cm.jemil.shared.utils;

import java.time.Clock;
import java.time.LocalDateTime;

public record CreatedAt(LocalDateTime value) implements Comparable<CreatedAt> {

    public CreatedAt {
        if (value == null) {
            throw new IllegalArgumentException("CreatedAt value is required");
        }
    }

    public static CreatedAt now() {
        return now(Clock.systemUTC());
    }

    public static CreatedAt now(Clock clock) {
        return of(LocalDateTime.now(clock));
    }

    public static CreatedAt of(LocalDateTime value) {
        if (isInTheFuture(value, Clock.systemUTC())) {
            throw new IllegalArgumentException("The given timestamp is already in the future");
        }
        return new CreatedAt(value);
    }

    public static CreatedAt of(LocalDateTime value, Clock clock) {
        if (isInTheFuture(value, clock)) {
            throw new IllegalArgumentException("The given timestamp is already in the future");
        }
        return new CreatedAt(value);
    }

    /** Trust persisted timestamps when rehydrating aggregates from the database. */
    public static CreatedAt reconstitute(LocalDateTime value) {
        return new CreatedAt(value);
    }

    private static boolean isInTheFuture(LocalDateTime value, Clock clock) {
        return LocalDateTime.now(clock).isBefore(value);
    }

    @Override
    public int compareTo(CreatedAt other) {
        return this.value.compareTo(other.value());
    }
}
