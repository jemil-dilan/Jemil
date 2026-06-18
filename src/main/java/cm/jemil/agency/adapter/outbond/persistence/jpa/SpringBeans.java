package cm.jemil.agency.adapter.outbond.persistence.jpa;

import cm.jemil.agency.adapter.outbond.persistence.jpa.repository.AgencyJpaRepository;
import cm.jemil.agency.adapter.outbond.persistence.jpa.repository.JpaAgencyRepository;
import cm.jemil.agency.adapter.outbond.persistence.jpa.repository.mapper.AgencyPersistenceMapper;
import cm.jemil.agency.domain.agency.AgencyRepository;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration("agencyPersistenceBeans")
@EntityScan(basePackages = {"cm.jemil.agency.adapter.outbond.persistence.jpa.entity"})
@EnableJpaRepositories(basePackages = {"cm.jemil.agency.adapter.outbond.persistence.jpa.repository"})
public class SpringBeans {

    @Bean("agencyRepository")
    public AgencyRepository agencyRepository(AgencyJpaRepository agencyJpaRepository, AgencyPersistenceMapper mapper) {
        return new JpaAgencyRepository(agencyJpaRepository, mapper);
    }
}
