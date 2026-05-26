package cm.jemil.payment.demo;

import cm.jemil.payment.demo.DemoId;
import cm.jemil.payment.demo.DemoName;

public record DemoCreatedEvent(DemoId id, DemoName demoName) {}
