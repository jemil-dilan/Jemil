package cm.jemil.agency.adapter.outbound.persistence.jpa;

import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.AgencyBranchSpringRepository;
import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.AgencyDemoSpringRepository;
import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.AgencySpringRepository;
import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.CitySpringRepository;
import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.JpaAgencyRepository;
import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.JpaBranchRepository;
import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.JpaCityRepository;
import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.JpaDemoRepository;
import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper.AgencyDemoJpaMapper;
import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper.AgencyJpaMapper;
import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper.CityJpaMapper;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.branch.BranchRepository;
import cm.jemil.agency.domain.city.CityRepository;
import cm.jemil.agency.domain.demo.DemoRepository;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration("agencyPersistenceBeans")
@EntityScan(basePackages = {"cm.jemil.agency.adapter.outbound.persistence.jpa.entity", "cm.jemil.shared.outbox"})
@EnableJpaRepositories(
        basePackages = {"cm.jemil.agency.adapter.outbound.persistence.jpa.repository", "cm.jemil.shared.outbox"})
public class SpringBeans {

    @Bean("agencyRepository")
    public AgencyRepository agencyRepository(AgencySpringRepository agencySpringRepository, AgencyJpaMapper mapper) {
        return new JpaAgencyRepository(agencySpringRepository, mapper);
    }

    @Bean("cityRepository")
    public CityRepository cityRepository(CitySpringRepository citySpringRepository, CityJpaMapper mapper) {
        return new JpaCityRepository(citySpringRepository, mapper);
    }

    @Bean("branchRepository")
    public BranchRepository branchRepository(
            AgencyBranchSpringRepository branchSpringRepository, AgencyJpaMapper mapper) {
        return new JpaBranchRepository(branchSpringRepository, mapper);
    }

    @Bean("agencyDemoRepository")
    public DemoRepository demoRepository(
            AgencyDemoSpringRepository demoSpringRepository, AgencyDemoJpaMapper demoJpaMapper) {
        return new JpaDemoRepository(demoSpringRepository, demoJpaMapper);
    }
}
