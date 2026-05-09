package cm.jemil.payment.demo;


import cm.jemil.auth.domain.demo.Demo;
import cm.jemil.auth.domain.demo.view.DemoView.DemoView1;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DemoRepository {

  void save(Demo value);

  List<DemoView1> loadAllView1();

  Optional<DemoView1> loadDemoByIdView1(UUID demoID);
}
