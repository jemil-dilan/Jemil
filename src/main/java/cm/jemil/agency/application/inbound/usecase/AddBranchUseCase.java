package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.application.inbound.usecase.AddBranchUseCaseImpl.Command;
import java.util.UUID;

public interface AddBranchUseCase {
    UUID execute(Command command);
}
