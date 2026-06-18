package cm.jemil.ticket.domain.demo;

import java.util.UUID;

public record DemoId(UUID value) {

    public DemoId() {
        this(UUID.randomUUID());
    }
}
