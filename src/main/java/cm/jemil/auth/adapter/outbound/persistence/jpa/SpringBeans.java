package cm.jemil.auth.adapter.outbound.persistence.jpa;

import cm.jemil.auth.adapter.outbound.persistence.jpa.repository.JpaUserRepository;
import cm.jemil.auth.adapter.outbound.persistence.jpa.repository.UserSpringRepository;
import cm.jemil.auth.adapter.outbound.persistence.jpa.repository.mapper.UserJpaMapper;
import cm.jemil.auth.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration("authJpaBeans")
@RequiredArgsConstructor
@EntityScan(basePackages = {"cm.jemil.auth.adapter.outbound.persistence.jpa.entity"})
@EnableJpaRepositories(basePackages = {"cm.jemil.auth.adapter.outbound.persistence.jpa.repository"})
public class SpringBeans {

    private final UserSpringRepository userSpringRepository;
    private final UserJpaMapper userJpaMapper;

    @Bean
    public UserRepository userRepository() {
        return new JpaUserRepository(userSpringRepository, userJpaMapper);
    }
}
