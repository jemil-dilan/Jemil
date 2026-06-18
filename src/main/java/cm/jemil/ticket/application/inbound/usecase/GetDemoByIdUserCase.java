package cm.jemil.ticket.application.inbound.usecase;

import cm.jemil.ticket.domain.demo.DemoNotFoundException;
import cm.jemil.ticket.domain.demo.DemoRepository;
import cm.jemil.ticket.domain.demo.view.DemoView;
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
