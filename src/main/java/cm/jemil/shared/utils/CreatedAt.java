package cm.jemil.shared.utils;

import java.time.LocalDateTime;

public record CreatedAt(LocalDateTime value) implements Comparable<CreatedAt> {

    public CreatedAt() {
        this(LocalDateTime.now());
    }

    public CreatedAt {
        if (isInTheFuture(value)) {
            throw new IllegalArgumentException("The given timestamp is already in the future");
        }
    }

    private static boolean isInTheFuture(LocalDateTime value) {
        // Allow up to 5 seconds of system clock drift/skew
        return LocalDateTime.now().plusSeconds(5).isBefore(value);
    }

    @Override
    public int compareTo(CreatedAt o) {
        return this.value.compareTo(o.value());
    }
}
