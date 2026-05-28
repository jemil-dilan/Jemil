package cm.jemil.auth.application;

import cm.jemil.auth.application.inbound.usecase.CreateDemoUserCase;
import cm.jemil.auth.application.inbound.usecase.GetAllDemoUserCase;
import cm.jemil.auth.application.inbound.usecase.GetDemoByIdUserCase;
import cm.jemil.auth.domain.demo.DemoRepository;
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
