package cm.jemil.booking.domain.bus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for bus domain operations.
 */
public interface BusRepository {

    /**
     * Save a bus.
     */
    Bus save(Bus bus);

    /**
     * Find a bus by ID.
     */
    Optional<Bus> findById(BusId id);

    /**
     * Find all buses by agency ID.
     */
    List<Bus> findByAgencyId(java.util.UUID agencyId);

    /**
     * Find a bus by ID (UUID).
     */
    Optional<Bus> findByUuid(java.util.UUID id);
}
