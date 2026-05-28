package cm.jemil.payment.application;

import cm.jemil.payment.application.inbound.usecase.CreateDemoUserCase;
import cm.jemil.payment.application.inbound.usecase.GetAllDemoUserCase;
import cm.jemil.payment.application.inbound.usecase.GetDemoByIdUserCase;
import cm.jemil.payment.demo.DemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SpringBeans {

    @Bean
    public CreateDemoUserCase createDemoUserCase(DemoRepository demoRepository) {
        return new CreateDemoUserCase(demoRepository);
    }

    @Bean
    public GetAllDemoUserCase getAllDemoUserCase(DemoRepository demoRepository) {
        return new GetAllDemoUserCase(demoRepository);
    }

    @Bean
    public GetDemoByIdUserCase getAlGetDemoByIdUserCase(DemoRepository demoRepository) {
        return new GetDemoByIdUserCase(demoRepository);
    }
}
