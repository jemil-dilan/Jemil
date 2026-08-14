package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.demo.DemoRepository;
import cm.jemil.agency.domain.demo.view.DemoView;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetDemoByIdUseCase {
    private final DemoRepository demoRepository;

    public DemoView execute(DemoQuery input) {
        return demoRepository.loadDemoByIdView1(input.demoID);
    }

    public record DemoQuery(UUID demoID, String fieldsToExtractCode) {}
}
