package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.application.inbound.usecase.CreateCityUseCaseImpl.Command;
import cm.jemil.agency.domain.city.CityId;

public interface CreateCityUseCase {
    CityId execute(Command command);
}
