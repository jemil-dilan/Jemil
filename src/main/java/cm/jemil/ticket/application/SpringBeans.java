package cm.jemil.ticket.application;

import cm.jemil.ticket.application.inbound.usecase.CreateDemoUseCase;
import cm.jemil.ticket.application.inbound.usecase.GetAllDemoUseCase;
import cm.jemil.ticket.application.inbound.usecase.GetDemoByIdUseCase;
import cm.jemil.ticket.domain.demo.DemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("ticketApplicationBeans")
@RequiredArgsConstructor
public class SpringBeans {

    @Bean("ticketCreateDemoUseCase")
    public CreateDemoUseCase createDemoUseCase(DemoRepository demoRepository) {
        return new CreateDemoUseCase(demoRepository);
    }

    @Bean("ticketGetAllDemoUseCase")
    public GetAllDemoUseCase getAllDemoUseCase(DemoRepository demoRepository) {
        return new GetAllDemoUseCase(demoRepository);
    }

    @Bean("ticketGetDemoByIdUseCase")
    public GetDemoByIdUseCase getAlGetDemoByIdUseCase(DemoRepository demoRepository) {
        return new GetDemoByIdUseCase(demoRepository);
    }
}
