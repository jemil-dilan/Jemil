package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.application.inbound.usecase.GetAllCitiesUseCaseImpl.Query;
import cm.jemil.agency.application.inbound.usecase.GetAllCitiesUseCaseImpl.Response;

public interface GetAllCitiesUseCase {
    Response execute(Query query);
}
