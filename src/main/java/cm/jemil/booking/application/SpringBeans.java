package cm.jemil.booking.application;

import cm.jemil.booking.application.inbound.usecase.CreateDemoUserCase;
import cm.jemil.booking.application.inbound.usecase.GetAllDemoUserCase;
import cm.jemil.booking.application.inbound.usecase.GetDemoByIdUserCase;
import cm.jemil.booking.demo.DemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("bookingApplicationBeans")
@RequiredArgsConstructor
public class SpringBeans {

    @Bean("bookingCreateDemoUserCase")
    public CreateDemoUserCase createDemoUserCase(DemoRepository demoRepository) {
        return new CreateDemoUserCase(demoRepository);
    }

    @Bean("bookingGetAllDemoUserCase")
    public GetAllDemoUserCase getAllDemoUserCase(DemoRepository demoRepository) {
        return new GetAllDemoUserCase(demoRepository);
    }

    @Bean("bookingGetDemoByIdUserCase")
    public GetDemoByIdUserCase getAlGetDemoByIdUserCase(DemoRepository demoRepository) {
        return new GetDemoByIdUserCase(demoRepository);
    }
}
