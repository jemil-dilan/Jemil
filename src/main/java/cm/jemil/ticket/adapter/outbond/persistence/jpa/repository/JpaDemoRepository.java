package cm.jemil.ticket.adapter.outbond.persistence.jpa.repository;

import cm.jemil.ticket.adapter.outbond.persistence.jpa.repository.mapper.DemoJpaMapper;
import cm.jemil.ticket.demo.Demo;
import cm.jemil.ticket.demo.DemoRepository;
import cm.jemil.ticket.demo.view.DemoView.DemoView1;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

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
