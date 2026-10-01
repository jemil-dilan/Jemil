package cm.nyi.agency.application.inbound.usecase;

import cm.nyi.agency.application.inbound.usecase.GetAllCitiesUseCaseImpl.Query;
import cm.nyi.agency.application.inbound.usecase.GetAllCitiesUseCaseImpl.Response;

public interface GetAllCitiesUseCase {
    Response execute(Query query);
}
