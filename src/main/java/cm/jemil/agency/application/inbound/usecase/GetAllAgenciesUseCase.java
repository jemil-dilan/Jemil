package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.application.inbound.usecase.GetAllAgenciesUseCaseImpl.Response;

public interface GetAllAgenciesUseCase {
    Response execute(GetAllAgenciesUseCaseImpl.Query query);
}
