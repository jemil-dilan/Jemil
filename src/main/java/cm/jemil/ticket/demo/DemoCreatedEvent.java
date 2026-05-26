package cm.jemil.ticket.demo;

import cm.jemil.ticket.demo.DemoId;
import cm.jemil.ticket.demo.DemoName;

public record DemoCreatedEvent(DemoId id, DemoName demoName) {}
