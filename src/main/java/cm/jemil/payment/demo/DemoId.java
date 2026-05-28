package cm.jemil.payment.demo;

import java.util.UUID;

public record DemoId(UUID value) {

    public DemoId() {
        this(UUID.randomUUID());
    }
}
