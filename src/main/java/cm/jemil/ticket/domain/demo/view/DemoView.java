package cm.jemil.ticket.domain.demo.view;

import java.util.UUID;

public sealed interface DemoView permits DemoView.DemoView1 {
    record DemoView1(UUID demoId, String demoName) implements DemoView {}
}
