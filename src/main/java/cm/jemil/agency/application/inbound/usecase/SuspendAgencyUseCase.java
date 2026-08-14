package cm.jemil.agency.application.inbound.usecase;

import java.util.UUID;

/**
 * Use case for suspending an agency.
 * Only ADMIN users can perform this action.
 */
public interface SuspendAgencyUseCase {
    void execute(UUID agencyId);
}
