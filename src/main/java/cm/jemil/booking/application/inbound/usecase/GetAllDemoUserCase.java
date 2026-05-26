package cm.jemil.booking.application.inbound.usecase;

import cm.jemil.booking.demo.DemoRepository;
import cm.jemil.booking.demo.view.DemoView;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetAllDemoUserCase {
  private final DemoRepository demoRepository;

  public List<DemoView.DemoView1> execute() {
      return demoRepository.loadAllView1();
  }
}
