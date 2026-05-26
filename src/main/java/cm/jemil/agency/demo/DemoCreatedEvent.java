package cm.jemil.agency.demo;

import cm.jemil.agency.demo.DemoId;
import cm.jemil.agency.demo.DemoName;

public record DemoCreatedEvent(DemoId id, DemoName demoName) {}
