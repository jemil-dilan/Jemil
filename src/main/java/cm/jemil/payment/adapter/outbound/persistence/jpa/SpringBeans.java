package cm.jemil.payment.adapter.outbound.persistence.jpa;

import cm.jemil.payment.adapter.outbound.persistence.jpa.repository.JpaDemoRepository;
import cm.jemil.payment.adapter.outbound.persistence.jpa.repository.PaymentDemoSpringRepository;
import cm.jemil.payment.adapter.outbound.persistence.jpa.repository.mapper.PaymentDemoJpaMapper;
import cm.jemil.payment.demo.DemoRepository;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration("paymentPersistenceBeans")
@EntityScan(basePackages = {"cm.jemil.payment.adapter.outbound.persistence.jpa.entity"})
@EnableJpaRepositories(basePackages = {"cm.jemil.payment.adapter.outbound.persistence.jpa.repository"})
public class SpringBeans {

    @Bean("paymentDemoRepository")
    public DemoRepository demoRepository(
            PaymentDemoSpringRepository demoSpringRepository, PaymentDemoJpaMapper demoJpaMapper) {
        return new JpaDemoRepository(demoSpringRepository, demoJpaMapper);
    }
}
