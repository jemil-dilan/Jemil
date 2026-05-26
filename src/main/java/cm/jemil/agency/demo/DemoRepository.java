package cm.jemil.agency.demo;


import cm.jemil.agency.demo.Demo;
import cm.jemil.agency.demo.view.DemoView.DemoView1;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DemoRepository {

  void save(Demo value);

  List<DemoView1> loadAllView1();

  Optional<DemoView1> loadDemoByIdView1(UUID demoID);
}
