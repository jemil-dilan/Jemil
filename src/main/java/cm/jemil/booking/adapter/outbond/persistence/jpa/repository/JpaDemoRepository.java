package cm.jemil.booking.adapter.outbond.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbond.persistence.jpa.repository.DemoSpringRepository;
import cm.jemil.booking.adapter.outbond.persistence.jpa.repository.mapper.DemoJpaMapper;
import cm.jemil.booking.demo.Demo;
import cm.jemil.booking.demo.DemoRepository;
import cm.jemil.booking.demo.view.DemoView.DemoView1;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class JpaDemoRepository implements DemoRepository {
  private final DemoSpringRepository demoSpringRepository;
  private final DemoJpaMapper demoJpaMapper;

  @Override
  public void save(Demo value) {
    demoSpringRepository.save(demoJpaMapper.fromDomain(value));
  }

  @Override
  public List<DemoView1> loadAllView1() {
    return demoSpringRepository.findAllAsView1();
  }

  @Override
  public Optional<DemoView1> loadDemoByIdView1(UUID demoID) {
    return demoSpringRepository.findByIdAsView1(demoID);
  }
}
