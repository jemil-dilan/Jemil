package cm.jemil.payment.application;

import cm.jemil.payment.application.inbound.usecase.CreateDemoUserCase;
import cm.jemil.payment.application.inbound.usecase.GetAllDemoUserCase;
import cm.jemil.payment.application.inbound.usecase.GetDemoByIdUserCase;
import cm.jemil.payment.demo.DemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("paymentApplicationBeans")
@RequiredArgsConstructor
public class SpringBeans {

    @Bean("paymentCreateDemoUserCase")
    public CreateDemoUserCase createDemoUserCase(DemoRepository demoRepository) {
        return new CreateDemoUserCase(demoRepository);
    }

    @Bean("paymentGetAllDemoUserCase")
    public GetAllDemoUserCase getAllDemoUserCase(DemoRepository demoRepository) {
        return new GetAllDemoUserCase(demoRepository);
    }

    @Bean("paymentGetDemoByIdUserCase")
    public GetDemoByIdUserCase getAlGetDemoByIdUserCase(DemoRepository demoRepository) {
        return new GetDemoByIdUserCase(demoRepository);
    }
}
