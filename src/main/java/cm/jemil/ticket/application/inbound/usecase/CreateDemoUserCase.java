package cm.jemil.ticket.application.inbound.usecase;

import cm.jemil.ticket.demo.Demo;
import cm.jemil.ticket.demo.DemoId;
import cm.jemil.ticket.demo.DemoName;
import cm.jemil.ticket.demo.DemoRepository;
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
