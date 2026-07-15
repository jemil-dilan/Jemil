package cm.jemil.agency.application;

import cm.jemil.agency.application.inbound.usecase.AddBranchUseCase;
import cm.jemil.agency.application.inbound.usecase.AddBranchUseCaseImpl;
import cm.jemil.agency.application.inbound.usecase.AddRouteService;
import cm.jemil.agency.application.inbound.usecase.AddRouteUseCase;
import cm.jemil.agency.application.inbound.usecase.CreateCityUseCase;
import cm.jemil.agency.application.inbound.usecase.CreateCityUseCaseImpl;
import cm.jemil.agency.application.inbound.usecase.CreateDemoUseCase;
import cm.jemil.agency.application.inbound.usecase.DeleteBranchUseCase;
import cm.jemil.agency.application.inbound.usecase.DeleteBranchUseCaseImpl;
import cm.jemil.agency.application.inbound.usecase.GetAgencyByIdUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAgencyByIdUseCaseImpl;
import cm.jemil.agency.application.inbound.usecase.GetAllAgenciesUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAllAgenciesUseCaseImpl;
import cm.jemil.agency.application.inbound.usecase.GetAllBranchesByAgencyUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAllBranchesByAgencyUseCaseImpl;
import cm.jemil.agency.application.inbound.usecase.GetAllCitiesUseCase;
import cm.jemil.agency.application.inbound.usecase.GetAllCitiesUseCaseImpl;
import cm.jemil.agency.application.inbound.usecase.GetAllDemoUseCase;
import cm.jemil.agency.application.inbound.usecase.GetBranchByIdUseCase;
import cm.jemil.agency.application.inbound.usecase.GetBranchByIdUseCaseImpl;
import cm.jemil.agency.application.inbound.usecase.GetDemoByIdUseCase;
import cm.jemil.agency.application.inbound.usecase.RegisterAgencyUseCase;
import cm.jemil.agency.application.inbound.usecase.RegisterAgencyUseCaseImpl;
import cm.jemil.agency.application.inbound.usecase.UpdateBranchUseCase;
import cm.jemil.agency.application.inbound.usecase.UpdateBranchUseCaseImpl;
import cm.jemil.agency.domain.agency.AgencyRegisteredEvent;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.branch.BranchRepository;
import cm.jemil.agency.domain.city.CityRepository;
import cm.jemil.agency.domain.demo.DemoRepository;
import cm.jemil.shared.events.DomainEventType;
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
        return new RegisterAgencyUseCaseImpl(agencyRepository, outboxEventPublisher);
    }

    @Bean("getAgencyByIdUseCase")
    public GetAgencyByIdUseCase getAgencyByIdUseCase(AgencyRepository agencyRepository) {
        return new GetAgencyByIdUseCaseImpl(agencyRepository);
    }

    @Bean("getAllAgenciesUseCase")
    public GetAllAgenciesUseCase getAllAgenciesUseCase(AgencyRepository agencyRepository) {
        return new GetAllAgenciesUseCaseImpl(agencyRepository);
    }

    @Bean("addRouteUseCase")
    public AddRouteUseCase addRouteUseCase(AgencyRepository agencyRepository) {
        return new AddRouteService(agencyRepository);
    }

    @Bean("agencyRegisteredEventType")
    public DomainEventType agencyRegisteredEventType() {
        return DomainEventType.from(AgencyRegisteredEvent.class);
    }

    @Bean("agencyCreateDemoUseCase")
    public CreateDemoUseCase createDemoUseCase(DemoRepository demoRepository) {
        return new CreateDemoUseCase(demoRepository);
    }

    @Bean("agencyGetAllDemoUseCase")
    public GetAllDemoUseCase getAllDemoUseCase(DemoRepository demoRepository) {
        return new GetAllDemoUseCase(demoRepository);
    }

    @Bean("agencyGetDemoByIdUseCase")
    public GetDemoByIdUseCase getDemoByIdUseCase(DemoRepository demoRepository) {
        return new GetDemoByIdUseCase(demoRepository);
    }

    @Bean("createCityUseCase")
    public CreateCityUseCase createCityUseCase(CityRepository cityRepository) {
        return new CreateCityUseCaseImpl(cityRepository);
    }

    @Bean("getAllCitiesUseCase")
    public GetAllCitiesUseCase getAllCitiesUseCase(CityRepository cityRepository) {
        return new GetAllCitiesUseCaseImpl(cityRepository);
    }

    @Bean("addBranchUseCase")
    public AddBranchUseCase addBranchUseCase(
            AgencyRepository agencyRepository, BranchRepository branchRepository, CityRepository cityRepository) {
        return new AddBranchUseCaseImpl(agencyRepository, branchRepository, cityRepository);
    }

    @Bean("getBranchByIdUseCase")
    public GetBranchByIdUseCase getBranchByIdUseCase(BranchRepository branchRepository) {
        return new GetBranchByIdUseCaseImpl(branchRepository);
    }

    @Bean("getAllBranchesByAgencyUseCase")
    public GetAllBranchesByAgencyUseCase getAllBranchesByAgencyUseCase(BranchRepository branchRepository) {
        return new GetAllBranchesByAgencyUseCaseImpl(branchRepository);
    }

    @Bean("deleteBranchUseCase")
    public DeleteBranchUseCase deleteBranchUseCase(BranchRepository branchRepository) {
        return new DeleteBranchUseCaseImpl(branchRepository);
    }

    @Bean("updateBranchUseCase")
    public UpdateBranchUseCase updateBranchUseCase(BranchRepository branchRepository) {
        return new UpdateBranchUseCaseImpl(branchRepository);
    }
}
