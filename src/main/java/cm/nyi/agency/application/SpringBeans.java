package cm.nyi.agency.application;

import cm.nyi.agency.application.inbound.usecase.AddBranchUseCase;
import cm.nyi.agency.application.inbound.usecase.AddBranchUseCaseImpl;
import cm.nyi.agency.application.inbound.usecase.AddRouteUseCase;
import cm.nyi.agency.application.inbound.usecase.AddRouteUseCaseImpl;
import cm.nyi.agency.application.inbound.usecase.CreateCityUseCase;
import cm.nyi.agency.application.inbound.usecase.CreateCityUseCaseImpl;
import cm.nyi.agency.application.inbound.usecase.DeleteBranchUseCase;
import cm.nyi.agency.application.inbound.usecase.DeleteBranchUseCaseImpl;
import cm.nyi.agency.application.inbound.usecase.GetAgencyByIdUseCase;
import cm.nyi.agency.application.inbound.usecase.GetAgencyByIdUseCaseImpl;
import cm.nyi.agency.application.inbound.usecase.GetAllAgenciesUseCase;
import cm.nyi.agency.application.inbound.usecase.GetAllAgenciesUseCaseImpl;
import cm.nyi.agency.application.inbound.usecase.GetAllBranchesByAgencyUseCase;
import cm.nyi.agency.application.inbound.usecase.GetAllBranchesByAgencyUseCaseImpl;
import cm.nyi.agency.application.inbound.usecase.GetAllCitiesUseCase;
import cm.nyi.agency.application.inbound.usecase.GetAllCitiesUseCaseImpl;
import cm.nyi.agency.application.inbound.usecase.GetBranchByIdUseCase;
import cm.nyi.agency.application.inbound.usecase.GetBranchByIdUseCaseImpl;
import cm.nyi.agency.application.inbound.usecase.RegisterAgencyUseCase;
import cm.nyi.agency.application.inbound.usecase.RegisterAgencyUseCaseImpl;
import cm.nyi.agency.application.inbound.usecase.SearchRoutesUseCase;
import cm.nyi.agency.application.inbound.usecase.SearchRoutesUseCaseImpl;
import cm.nyi.agency.application.inbound.usecase.SuspendAgencyUseCase;
import cm.nyi.agency.application.inbound.usecase.SuspendAgencyUseCaseImpl;
import cm.nyi.agency.application.inbound.usecase.UpdateBranchUseCase;
import cm.nyi.agency.application.inbound.usecase.UpdateBranchUseCaseImpl;
import cm.nyi.agency.domain.agency.AgencyRegisteredEvent;
import cm.nyi.agency.domain.agency.AgencyRepository;
import cm.nyi.agency.domain.branch.BranchRepository;
import cm.nyi.agency.domain.city.CityRepository;
import cm.nyi.shared.events.DomainEventType;
import cm.nyi.shared.outbox.OutboxEventPublisher;
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

    @Bean("searchRoutesUseCase")
    public SearchRoutesUseCase searchRoutesUseCase(AgencyRepository agencyRepository) {
        return new SearchRoutesUseCaseImpl(agencyRepository);
    }

    @Bean("suspendAgencyUseCase")
    public SuspendAgencyUseCase suspendAgencyUseCase(AgencyRepository agencyRepository) {
        return new SuspendAgencyUseCaseImpl(agencyRepository);
    }

    @Bean("addRouteUseCase")
    public AddRouteUseCase addRouteUseCase(AgencyRepository agencyRepository) {
        return new AddRouteUseCaseImpl(agencyRepository);
    }

    @Bean("agencyRegisteredEventType")
    public DomainEventType agencyRegisteredEventType() {
        return DomainEventType.from(AgencyRegisteredEvent.class);
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
