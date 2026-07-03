package cm.jemil.payment.application.inbound.usecase;

import cm.jemil.payment.demo.DemoRepository;
import cm.jemil.payment.demo.view.DemoView;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAllDemoUseCase {
    private final DemoRepository demoRepository;

    public List<DemoView.DemoView1> execute() {
        return demoRepository.loadAllView1();
    }
}
