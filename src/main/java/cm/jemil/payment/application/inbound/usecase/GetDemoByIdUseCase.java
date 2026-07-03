package cm.jemil.payment.application.inbound.usecase;

import cm.jemil.payment.demo.DemoNotFoundException;
import cm.jemil.payment.demo.DemoRepository;
import cm.jemil.payment.demo.view.DemoView;
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
