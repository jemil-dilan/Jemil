package cm.jemil.trip.application.inbound.usecase;

import cm.jemil.trip.demo.Demo;
import cm.jemil.trip.demo.DemoId;
import cm.jemil.trip.demo.DemoName;
import cm.jemil.trip.demo.DemoRepository;
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
