package cm.nyi.agency.application.inbound.usecase;

import cm.nyi.agency.application.inbound.usecase.CreateCityUseCaseImpl.Command;
import cm.nyi.agency.domain.city.CityId;

public interface CreateCityUseCase {
    CityId execute(Command command);
}
