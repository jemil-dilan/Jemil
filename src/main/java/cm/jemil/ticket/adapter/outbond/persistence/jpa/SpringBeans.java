package cm.jemil.ticket.adapter.outbond.persistence.jpa;

import cm.jemil.ticket.adapter.outbond.persistence.jpa.repository.JpaDemoRepository;
import cm.jemil.ticket.adapter.outbond.persistence.jpa.repository.TicketDemoSpringRepository;
import cm.jemil.ticket.adapter.outbond.persistence.jpa.repository.mapper.TicketDemoJpaMapper;
import cm.jemil.ticket.domain.demo.DemoRepository;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration("ticketPersistenceBeans")
@EntityScan(basePackages = {"cm.jemil.ticket.adapter.outbond.persistence.jpa.entity"})
@EnableJpaRepositories(basePackages = {"cm.jemil.ticket.adapter.outbond.persistence.jpa.repository"})
public class SpringBeans {

    @Bean("ticketDemoRepository")
    public DemoRepository demoRepository(
            TicketDemoSpringRepository demoSpringRepository, TicketDemoJpaMapper demoJpaMapper) {
        return new JpaDemoRepository(demoSpringRepository, demoJpaMapper);
    }
}
