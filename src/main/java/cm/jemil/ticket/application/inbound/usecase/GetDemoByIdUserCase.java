package cm.jemil.ticket.application.inbound.usecase;

import cm.jemil.auth.adapter.outbond.persistence.jpa.entity.DemoNotFoundException;
import cm.jemil.auth.domain.demo.DemoRepository;
import cm.jemil.auth.domain.demo.view.DemoView;
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
