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
        return LocalDateTime.now().isBefore(value);
    }

    @Override
    public int compareTo(CreatedAt o) {
        return this.value.compareTo(o.value());
    }
}
