package cm.jemil.auth.adapter.outbond.persistence.jpa;

import cm.jemil.auth.adapter.outbond.persistence.jpa.repository.AuthDemoSpringRepository;
import cm.jemil.auth.adapter.outbond.persistence.jpa.repository.JpaDemoRepository;
import cm.jemil.auth.adapter.outbond.persistence.jpa.repository.mapper.AuthDemoJpaMapper;
import cm.jemil.auth.domain.demo.DemoRepository;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration("authPersistenceBeans")
@EntityScan(basePackages = {"cm.jemil.auth.adapter.outbond.persistence.jpa.entity"})
@EnableJpaRepositories(basePackages = {"cm.jemil.auth.adapter.outbond.persistence.jpa.repository"})
public class SpringBeans {

    @Bean("authDemoRepository")
    public DemoRepository demoRepository(
            AuthDemoSpringRepository demoSpringRepository, AuthDemoJpaMapper demoJpaMapper) {
        return new JpaDemoRepository(demoSpringRepository, demoJpaMapper);
    }
}
