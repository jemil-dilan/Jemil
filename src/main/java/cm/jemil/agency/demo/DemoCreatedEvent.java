package cm.jemil.agency.demo;

import cm.jemil.auth.domain.demo.DemoId;
import cm.jemil.auth.domain.demo.DemoName;

public record DemoCreatedEvent(DemoId id, DemoName demoName) {}
