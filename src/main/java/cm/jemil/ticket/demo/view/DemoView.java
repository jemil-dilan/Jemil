package cm.jemil.ticket.demo.view;

import java.util.UUID;

public sealed interface DemoView permits DemoView.DemoView1 {
  record DemoView1(UUID demoId, String demoName) implements DemoView {}
}
