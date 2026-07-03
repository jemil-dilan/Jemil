package cm.jemil.auth.application.inbound.usecase;

import cm.jemil.auth.demo.DemoNotFoundException;
import cm.jemil.auth.domain.demo.DemoRepository;
import cm.jemil.auth.domain.demo.view.DemoView;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetDemoByIdUseCase {
    private final DemoRepository demoRepository;

    public DemoView execute(DemoQuery input) {
        return demoRepository.loadDemoByIdView1(input.demoID).orElseThrow(DemoNotFoundException::new);
    }

    public record DemoQuery(UUID demoID, String fieldsToExtractCode) {}
}
