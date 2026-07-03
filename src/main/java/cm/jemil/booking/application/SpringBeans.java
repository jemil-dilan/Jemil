package cm.jemil.booking.application;

import cm.jemil.booking.application.inbound.usecase.CreateDemoUseCase;
import cm.jemil.booking.application.inbound.usecase.GetAllDemoUseCase;
import cm.jemil.booking.application.inbound.usecase.GetDemoByIdUseCase;
import cm.jemil.booking.demo.DemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("bookingApplicationBeans")
@RequiredArgsConstructor
public class SpringBeans {

    @Bean("bookingCreateDemoUseCase")
    public CreateDemoUseCase createDemoUseCase(DemoRepository demoRepository) {
        return new CreateDemoUseCase(demoRepository);
    }

    @Bean("bookingGetAllDemoUseCase")
    public GetAllDemoUseCase getAllDemoUseCase(DemoRepository demoRepository) {
        return new GetAllDemoUseCase(demoRepository);
    }

    @Bean("bookingGetDemoByIdUseCase")
    public GetDemoByIdUseCase getAlGetDemoByIdUseCase(DemoRepository demoRepository) {
        return new GetDemoByIdUseCase(demoRepository);
    }
}
