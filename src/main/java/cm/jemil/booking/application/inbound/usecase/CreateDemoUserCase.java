package cm.jemil.booking.application.inbound.usecase;

import cm.jemil.booking.demo.Demo;
import cm.jemil.booking.demo.DemoId;
import cm.jemil.booking.demo.DemoName;
import cm.jemil.booking.demo.DemoRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class CreateDemoUserCase {
  private final DemoRepository demoRepository;

  public UUID execute(CreateDemoCommand input) {
    Demo demo = Demo.of(new DemoId(), new DemoName(input.name));
    demoRepository.save(demo);
    return demo.id();
  }

  public record CreateDemoCommand(String name) {}
}
