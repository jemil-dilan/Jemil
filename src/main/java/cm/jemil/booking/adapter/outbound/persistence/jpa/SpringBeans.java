package cm.jemil.booking.adapter.outbound.persistence.jpa;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration("bookingPersistenceBeans")
@EntityScan(basePackages = {"cm.jemil.booking.adapter.outbound.persistence.jpa.entity"})
@EnableJpaRepositories(basePackages = {"cm.jemil.booking.adapter.outbound.persistence.jpa.repository"})
public class SpringBeans {}
