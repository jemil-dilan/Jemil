package cm.jemil.ticket.application.inbound.usecase;

import cm.jemil.ticket.demo.DemoRepository;
import cm.jemil.ticket.demo.view.DemoView;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetAllDemoUserCase {
  private final DemoRepository demoRepository;

  public List<DemoView.DemoView1> execute() {
      return demoRepository.loadAllView1();
  }
}
