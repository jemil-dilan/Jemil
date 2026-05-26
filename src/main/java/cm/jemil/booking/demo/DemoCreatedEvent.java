package cm.jemil.booking.demo;

import cm.jemil.booking.demo.DemoId;
import cm.jemil.booking.demo.DemoName;

public record DemoCreatedEvent(DemoId id, DemoName demoName) {}
