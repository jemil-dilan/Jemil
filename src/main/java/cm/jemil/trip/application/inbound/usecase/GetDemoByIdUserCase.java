package cm.jemil.trip.application.inbound.usecase;

import cm.jemil.trip.demo.DemoNotFoundException;
import cm.jemil.trip.demo.DemoRepository;
import cm.jemil.trip.demo.view.DemoView;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetDemoByIdUserCase {
    private final DemoRepository demoRepository;

    public DemoView execute(DemoQuery input) {
        return demoRepository.loadDemoByIdView1(input.demoID).orElseThrow(DemoNotFoundException::new);
    }

    public record DemoQuery(UUID demoID, String fieldsToExtractCode) {}
}
