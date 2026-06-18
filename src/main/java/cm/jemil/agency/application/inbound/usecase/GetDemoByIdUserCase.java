package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.demo.DemoRepository;
import cm.jemil.agency.domain.demo.view.DemoView;
import cm.jemil.agency.domain.exception.DemoNotFoundException;
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
