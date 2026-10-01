package cm.nyi.agency.adapter.outbound.persistence.jpa;

import cm.nyi.agency.adapter.outbound.persistence.jpa.repository.AgencyBranchSpringRepository;
import cm.nyi.agency.adapter.outbound.persistence.jpa.repository.AgencySpringRepository;
import cm.nyi.agency.adapter.outbound.persistence.jpa.repository.CitySpringRepository;
import cm.nyi.agency.adapter.outbound.persistence.jpa.repository.JpaAgencyRepository;
import cm.nyi.agency.adapter.outbound.persistence.jpa.repository.JpaBranchRepository;
import cm.nyi.agency.adapter.outbound.persistence.jpa.repository.JpaCityRepository;
import cm.nyi.agency.adapter.outbound.persistence.jpa.repository.mapper.AgencyJpaMapper;
import cm.nyi.agency.adapter.outbound.persistence.jpa.repository.mapper.CityJpaMapper;
import cm.nyi.agency.domain.agency.AgencyRepository;
import cm.nyi.agency.domain.branch.BranchRepository;
import cm.nyi.agency.domain.city.CityRepository;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration("agencyPersistenceBeans")
@EntityScan(basePackages = {"cm.nyi.agency.adapter.outbound.persistence.jpa.entity", "cm.nyi.shared.outbox"})
@EnableJpaRepositories(
        basePackages = {"cm.nyi.agency.adapter.outbound.persistence.jpa.repository", "cm.nyi.shared.outbox"})
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
}
