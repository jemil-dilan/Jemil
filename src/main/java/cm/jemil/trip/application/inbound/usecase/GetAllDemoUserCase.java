package cm.jemil.trip.application.inbound.usecase;

import cm.jemil.trip.demo.DemoRepository;
import cm.jemil.trip.demo.view.DemoView;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAllDemoUserCase {
    private final DemoRepository demoRepository;

    public List<DemoView.DemoView1> execute() {
        return demoRepository.loadAllView1();
    }
}
