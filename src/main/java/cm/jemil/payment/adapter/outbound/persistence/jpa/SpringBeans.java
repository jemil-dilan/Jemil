package cm.jemil.payment.adapter.outbound.persistence.jpa;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration("paymentPersistenceBeans")
@EntityScan(basePackages = {"cm.jemil.payment.adapter.outbound.persistence.jpa.entity"})
@EnableJpaRepositories(basePackages = {"cm.jemil.payment.adapter.outbound.persistence.jpa.repository"})
public class SpringBeans {}
