package cm.jemil.trip.application;

import cm.jemil.trip.application.inbound.usecase.CreateDemoUserCase;
import cm.jemil.trip.application.inbound.usecase.GetAllDemoUserCase;
import cm.jemil.trip.application.inbound.usecase.GetDemoByIdUserCase;
import cm.jemil.trip.demo.DemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("tripApplicationBeans")
@RequiredArgsConstructor
public class SpringBeans {

    @Bean("tripCreateDemoUserCase")
    public CreateDemoUserCase createDemoUserCase(DemoRepository demoRepository) {
        return new CreateDemoUserCase(demoRepository);
    }

    @Bean("tripGetAllDemoUserCase")
    public GetAllDemoUserCase getAllDemoUserCase(DemoRepository demoRepository) {
        return new GetAllDemoUserCase(demoRepository);
    }

    @Bean("tripGetDemoByIdUserCase")
    public GetDemoByIdUserCase getDemoByIdUserCase(DemoRepository demoRepository) {
        return new GetDemoByIdUserCase(demoRepository);
    }
}
