package cm.jemil.auth.application;

import cm.jemil.auth.application.inbound.usecase.CreateDemoUseCase;
import cm.jemil.auth.application.inbound.usecase.GetAllDemoUseCase;
import cm.jemil.auth.application.inbound.usecase.GetDemoByIdUseCase;
import cm.jemil.auth.domain.demo.DemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("authApplicationBeans")
@RequiredArgsConstructor
public class SpringBeans {

    @Bean("authCreateDemoUseCase")
    public CreateDemoUseCase createDemoUseCase(DemoRepository demoRepository) {
        return new CreateDemoUseCase(demoRepository);
    }

    @Bean("authGetAllDemoUseCase")
    public GetAllDemoUseCase getAllDemoUseCase(DemoRepository demoRepository) {
        return new GetAllDemoUseCase(demoRepository);
    }

    @Bean("authGetDemoByIdUseCase")
    public GetDemoByIdUseCase getAlGetDemoByIdUseCase(DemoRepository demoRepository) {
        return new GetDemoByIdUseCase(demoRepository);
    }
}
