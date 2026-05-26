package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.demo.Demo;
import cm.jemil.agency.demo.DemoId;
import cm.jemil.agency.demo.DemoName;
import cm.jemil.agency.demo.DemoRepository;
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
