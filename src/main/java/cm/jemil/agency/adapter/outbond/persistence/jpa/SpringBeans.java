package cm.jemil.agency.adapter.outbond.persistence.jpa;

import cm.jemil.agency.adapter.outbond.persistence.jpa.repository.DemoSpringRepository;
import cm.jemil.agency.adapter.outbond.persistence.jpa.repository.JpaDemoRepository;
import cm.jemil.agency.adapter.outbond.persistence.jpa.repository.mapper.DemoJpaMapper;
import cm.jemil.agency.demo.DemoRepository;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration("agencyPersistenceBeans")
@EntityScan(basePackages = {"cm.jemil.agency.adapter.outbond.persistence.jpa.entity"})
@EnableJpaRepositories(basePackages = {"cm.jemil.agency.adapter.outbond.persistence.jpa.repository"})
public class SpringBeans {

    @Bean("agencyDemoRepository")
    public DemoRepository demoRepository(DemoSpringRepository demoSpringRepository, DemoJpaMapper demoJpaMapper) {
        return new JpaDemoRepository(demoSpringRepository, demoJpaMapper);
    }
}
