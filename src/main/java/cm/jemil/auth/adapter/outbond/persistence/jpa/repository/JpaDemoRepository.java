package cm.jemil.auth.adapter.outbond.persistence.jpa.repository;

import cm.jemil.auth.adapter.outbond.persistence.jpa.repository.mapper.AuthDemoJpaMapper;
import cm.jemil.auth.domain.demo.Demo;
import cm.jemil.auth.domain.demo.DemoRepository;
import cm.jemil.auth.domain.demo.view.DemoView.DemoView1;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaDemoRepository implements DemoRepository {
    private final AuthDemoSpringRepository demoSpringRepository;
    private final AuthDemoJpaMapper demoJpaMapper;

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
