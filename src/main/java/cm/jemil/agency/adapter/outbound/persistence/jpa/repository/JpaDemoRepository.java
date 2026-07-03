package cm.jemil.agency.adapter.outbound.persistence.jpa.repository;

import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper.AgencyDemoJpaMapper;
import cm.jemil.agency.domain.demo.Demo;
import cm.jemil.agency.domain.demo.DemoRepository;
import cm.jemil.agency.domain.demo.view.DemoView.DemoView1;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaDemoRepository implements DemoRepository {
    private final AgencyDemoSpringRepository demoSpringRepository;
    private final AgencyDemoJpaMapper demoJpaMapper;

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
