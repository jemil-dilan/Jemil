package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.demo.DemoNotFoundException;
import cm.jemil.agency.demo.DemoRepository;
import cm.jemil.agency.demo.view.DemoView;
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
