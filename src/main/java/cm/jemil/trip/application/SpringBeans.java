package cm.jemil.trip.application;

import cm.jemil.trip.application.inbound.usecase.CreateDemoUseCase;
import cm.jemil.trip.application.inbound.usecase.GetAllDemoUseCase;
import cm.jemil.trip.application.inbound.usecase.GetDemoByIdUseCase;
import cm.jemil.trip.demo.DemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("tripApplicationBeans")
@RequiredArgsConstructor
public class SpringBeans {

    @Bean("tripCreateDemoUseCase")
    public CreateDemoUseCase createDemoUseCase(DemoRepository demoRepository) {
        return new CreateDemoUseCase(demoRepository);
    }

    @Bean("tripGetAllDemoUseCase")
    public GetAllDemoUseCase getAllDemoUseCase(DemoRepository demoRepository) {
        return new GetAllDemoUseCase(demoRepository);
    }

    @Bean("tripGetDemoByIdUseCase")
    public GetDemoByIdUseCase getDemoByIdUseCase(DemoRepository demoRepository) {
        return new GetDemoByIdUseCase(demoRepository);
    }
}
