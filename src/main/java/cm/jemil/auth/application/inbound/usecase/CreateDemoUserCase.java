package cm.jemil.auth.application.inbound.usecase;

import cm.jemil.auth.domain.demo.Demo;
import cm.jemil.auth.domain.demo.DemoId;
import cm.jemil.auth.domain.demo.DemoName;
import cm.jemil.auth.domain.demo.DemoRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

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
