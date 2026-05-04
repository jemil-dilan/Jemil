package cm.jemil.booking.adapter.outbond.persistence.jpa;

import cm.jemil.booking.adapter.outbond.persistence.jpa.repository.DemoSpringRepository;
import cm.jemil.booking.adapter.outbond.persistence.jpa.repository.JpaDemoRepository;
import cm.jemil.booking.adapter.outbond.persistence.jpa.repository.mapper.DemoJpaMapper;
import cm.jemil.booking.demo.DemoRepository;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration("bookingPersistenceBeans")
@EntityScan(basePackages = {"cm.jemil.booking.adapter.outbond.persistence.jpa.entity"})
@EnableJpaRepositories(basePackages = {"cm.jemil.booking.adapter.outbond.persistence.jpa.repository"})
public class SpringBeans {

    @Bean("bookingDemoRepository")
    public DemoRepository demoRepository(DemoSpringRepository demoSpringRepository, DemoJpaMapper demoJpaMapper) {
        return new JpaDemoRepository(demoSpringRepository, demoJpaMapper);
    }
}
