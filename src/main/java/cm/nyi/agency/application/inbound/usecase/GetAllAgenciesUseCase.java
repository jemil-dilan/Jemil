package cm.nyi.agency.application.inbound.usecase;

import cm.nyi.agency.application.inbound.usecase.GetAllAgenciesUseCaseImpl.Response;

public interface GetAllAgenciesUseCase {
    Response execute(GetAllAgenciesUseCaseImpl.Query query);
}
