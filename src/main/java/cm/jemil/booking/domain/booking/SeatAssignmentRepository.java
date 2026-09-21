package cm.jemil.booking.domain.booking;

import java.util.List;

/**
 * Repository interface for seat assignment domain operations.
 */
public interface SeatAssignmentRepository {

    /**
     * Save multiple seat assignments.
     */
    List<SeatAssignment> saveAll(List<SeatAssignment> seatAssignments);
}
