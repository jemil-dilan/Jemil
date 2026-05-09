package cm.jemil.agency.demo;

import java.util.UUID;

public record DemoId(UUID value) {

  public DemoId() {
    this(UUID.randomUUID());
  }
}
