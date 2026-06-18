package cm.jemil.trip.adapter.outbond.persistence.jpa;

import cm.jemil.trip.adapter.outbond.persistence.jpa.repository.JpaDemoRepository;
import cm.jemil.trip.adapter.outbond.persistence.jpa.repository.TripDemoSpringRepository;
import cm.jemil.trip.adapter.outbond.persistence.jpa.repository.mapper.TripDemoJpaMapper;
import cm.jemil.trip.demo.DemoRepository;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration("tripPersistenceBeans")
@EntityScan(basePackages = {"cm.jemil.trip.adapter.outbond.persistence.jpa.entity"})
@EnableJpaRepositories(basePackages = {"cm.jemil.trip.adapter.outbond.persistence.jpa.repository"})
public class SpringBeans {

    @Bean("tripDemoRepository")
    public DemoRepository demoRepository(TripDemoSpringRepository demoSpringRepository, TripDemoJpaMapper demoJpaMapper) {
        return new JpaDemoRepository(demoSpringRepository, demoJpaMapper);
    }
}
