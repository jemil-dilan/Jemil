package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.application.inbound.usecase.AddRouteUseCaseImpl.Command;

public interface AddRouteUseCase {
    void execute(Command command);
}
