package cm.jemil.agency.application;

import cm.jemil.agency.application.inbound.usecase.CreateDemoUserCase;
import cm.jemil.agency.application.inbound.usecase.GetAgencyByIdService;
import cm.jemil.agency.application.inbound.usecase.GetAgencyByIdUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAllAgenciesService;
import cm.jemil.agency.application.inbound.usecase.GetAllAgenciesUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAllDemoUserCase;
import cm.jemil.agency.application.inbound.usecase.GetDemoByIdUserCase;
import cm.jemil.agency.application.inbound.usecase.RegisterAgencyService;
import cm.jemil.agency.application.inbound.usecase.RegisterAgencyUseCase;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.demo.DemoRepository;
import cm.jemil.shared.outbox.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("agencyApplicationBeans")
@RequiredArgsConstructor
public class SpringBeans {

    private final OutboxEventPublisher outboxEventPublisher;

    @Bean("registerAgencyUseCase")
    public RegisterAgencyUseCase registerAgencyUseCase(AgencyRepository agencyRepository) {
        return new RegisterAgencyService(agencyRepository, outboxEventPublisher);
    }

    @Bean("getAgencyByIdUseCase")
    public GetAgencyByIdUseCase getAgencyByIdUseCase(AgencyRepository agencyRepository) {
        return new GetAgencyByIdService(agencyRepository);
    }

    @Bean("getAllAgenciesUseCase")
    public GetAllAgenciesUseCase getAllAgenciesUseCase(AgencyRepository agencyRepository) {
        return new GetAllAgenciesService(agencyRepository);
    }

    @Bean("agencyCreateDemoUserCase")
    public CreateDemoUserCase createDemoUserCase(DemoRepository demoRepository) {
        return new CreateDemoUserCase(demoRepository);
    }

    @Bean("agencyGetAllDemoUserCase")
    public GetAllDemoUserCase getAllDemoUserCase(DemoRepository demoRepository) {
        return new GetAllDemoUserCase(demoRepository);
    }

    @Bean("agencyGetDemoByIdUserCase")
    public GetDemoByIdUserCase getDemoByIdUserCase(DemoRepository demoRepository) {
        return new GetDemoByIdUserCase(demoRepository);
    }
}
