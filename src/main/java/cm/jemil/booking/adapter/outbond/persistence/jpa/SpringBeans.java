package cm.jemil.booking.adapter.outbond.persistence.jpa;

import cm.jemil.auth.adapter.outbond.persistence.jpa.repository.DemoSpringRepository;
import cm.jemil.auth.adapter.outbond.persistence.jpa.repository.JpaDemoRepository;
import cm.jemil.auth.adapter.outbond.persistence.jpa.repository.mapper.DemoJpaMapper;
import cm.jemil.auth.domain.demo.DemoRepository;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {"cm.jemil.auth.adapter.outbound.persistence.jpa.entity"})
@EnableJpaRepositories(
    basePackages = {"cm.jemil.auth.adapter.outbound.persistence.jpa.repository"})
public class SpringBeans {

  @Bean
  public DemoRepository demoRepository(
      DemoSpringRepository demoSpringRepository, DemoJpaMapper demoJpaMapper) {
    return new JpaDemoRepository(demoSpringRepository, demoJpaMapper);
  }
}
