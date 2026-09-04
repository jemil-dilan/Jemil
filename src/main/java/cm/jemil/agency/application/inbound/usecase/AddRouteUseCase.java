package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.application.inbound.usecase.AddRouteUseCaseImpl.Command;
import java.util.UUID;

public interface AddRouteUseCase {
    UUID execute(Command command);
}
