package cm.jemil.ticket.application;

import cm.jemil.ticket.application.inbound.usecase.CreateDemoUserCase;
import cm.jemil.ticket.application.inbound.usecase.GetAllDemoUserCase;
import cm.jemil.ticket.application.inbound.usecase.GetDemoByIdUserCase;
import cm.jemil.ticket.domain.demo.DemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("ticketApplicationBeans")
@RequiredArgsConstructor
public class SpringBeans {

    @Bean("ticketCreateDemoUserCase")
    public CreateDemoUserCase createDemoUserCase(DemoRepository demoRepository) {
        return new CreateDemoUserCase(demoRepository);
    }

    @Bean("ticketGetAllDemoUserCase")
    public GetAllDemoUserCase getAllDemoUserCase(DemoRepository demoRepository) {
        return new GetAllDemoUserCase(demoRepository);
    }

    @Bean("ticketGetDemoByIdUserCase")
    public GetDemoByIdUserCase getAlGetDemoByIdUserCase(DemoRepository demoRepository) {
        return new GetDemoByIdUserCase(demoRepository);
    }
}
