package cm.jemil.agency.domain.demo;

import cm.jemil.agency.domain.demo.view.DemoView.DemoView1;
import java.util.List;
import java.util.UUID;

public interface DemoRepository {

    void save(Demo value);

    List<DemoView1> loadAllView1();

    DemoView1 loadDemoByIdView1(UUID demoID);
}
