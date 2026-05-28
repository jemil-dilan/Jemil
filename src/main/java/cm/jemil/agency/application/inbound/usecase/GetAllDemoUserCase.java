package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.demo.DemoRepository;
import cm.jemil.agency.demo.view.DemoView;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAllDemoUserCase {
    private final DemoRepository demoRepository;

    public List<DemoView.DemoView1> execute() {
        return demoRepository.loadAllView1();
    }
}
