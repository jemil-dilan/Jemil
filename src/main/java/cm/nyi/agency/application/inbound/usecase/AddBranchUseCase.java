package cm.nyi.agency.application.inbound.usecase;

import cm.nyi.agency.application.inbound.usecase.AddBranchUseCaseImpl.Command;
import java.util.UUID;

public interface AddBranchUseCase {
    UUID execute(Command command);
}
