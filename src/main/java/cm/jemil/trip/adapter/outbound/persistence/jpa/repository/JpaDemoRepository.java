package cm.jemil.trip.adapter.outbound.persistence.jpa.repository;

import cm.jemil.trip.adapter.outbound.persistence.jpa.repository.mapper.TripDemoJpaMapper;
import cm.jemil.trip.demo.Demo;
import cm.jemil.trip.demo.DemoRepository;
import cm.jemil.trip.demo.view.DemoView.DemoView1;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaDemoRepository implements DemoRepository {
    private final TripDemoSpringRepository demoSpringRepository;
    private final TripDemoJpaMapper demoJpaMapper;

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
