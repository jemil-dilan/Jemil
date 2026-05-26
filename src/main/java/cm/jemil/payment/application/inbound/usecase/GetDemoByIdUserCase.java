package cm.jemil.payment.application.inbound.usecase;

import cm.jemil.payment.demo.DemoNotFoundException;
import cm.jemil.payment.demo.DemoRepository;
import cm.jemil.payment.demo.view.DemoView;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class GetDemoByIdUserCase {
  private final DemoRepository demoRepository;

  public DemoView execute(DemoQuery input) {
         return demoRepository.loadDemoByIdView1(input.demoID).orElseThrow(DemoNotFoundException::new);
  }

  public record DemoQuery(UUID demoID, String fieldsToExtractCode) {}
}
