package cm.jemil.payment.application;

import cm.jemil.payment.application.inbound.usecase.CreateDemoUseCase;
import cm.jemil.payment.application.inbound.usecase.GetAllDemoUseCase;
import cm.jemil.payment.application.inbound.usecase.GetDemoByIdUseCase;
import cm.jemil.payment.demo.DemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("paymentApplicationBeans")
@RequiredArgsConstructor
public class SpringBeans {

    @Bean("paymentCreateDemoUseCase")
    public CreateDemoUseCase createDemoUseCase(DemoRepository demoRepository) {
        return new CreateDemoUseCase(demoRepository);
    }

    @Bean("paymentGetAllDemoUseCase")
    public GetAllDemoUseCase getAllDemoUseCase(DemoRepository demoRepository) {
        return new GetAllDemoUseCase(demoRepository);
    }

    @Bean("paymentGetDemoByIdUseCase")
    public GetDemoByIdUseCase getAlGetDemoByIdUseCase(DemoRepository demoRepository) {
        return new GetDemoByIdUseCase(demoRepository);
    }
}
