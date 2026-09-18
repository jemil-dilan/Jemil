package cm.jemil.booking.adapter.outbound.persistence.jpa.repository.mapper;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.SeatAssignmentJpa;
import cm.jemil.booking.domain.booking.BookingId;
import cm.jemil.booking.domain.booking.SeatAssignment;
import cm.jemil.booking.domain.seat.SeatNumber;
import cm.jemil.booking.domain.seat.SeatStatus;
import cm.jemil.booking.domain.trip.TripId;
import cm.jemil.shared.utils.CreatedAt;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Mapper between SeatAssignment domain entity and SeatAssignmentJpa entity.
 */
@Component
public class SeatAssignmentJpaMapper {

    public SeatAssignmentJpa toJpa(SeatAssignment domain) {
        Objects.requireNonNull(domain, "SeatAssignment domain entity cannot be null");

        SeatAssignmentJpa jpa = new SeatAssignmentJpa();
        jpa.setId(domain.getId().value());
        jpa.setTripId(domain.getTripId().value());
        jpa.setSeatNo(domain.getSeatNo().value());
        jpa.setBookingId(domain.getBookingId().value());
        jpa.setStatus(domain.getStatus().name());
        jpa.setCreatedAt(domain.getCreatedAt().value().atOffset(java.time.ZoneOffset.UTC));
        return jpa;
    }

    public SeatAssignment toDomain(SeatAssignmentJpa jpa) {
        Objects.requireNonNull(jpa, "SeatAssignmentJpa entity cannot be null");

        return SeatAssignment.create(
                new TripId(jpa.getTripId()),
                new SeatNumber(jpa.getSeatNo()),
                new BookingId(jpa.getBookingId()),
                SeatStatus.valueOf(jpa.getStatus()),
                CreatedAt.reconstitute(jpa.getCreatedAt().toLocalDateTime()));
    }

    public void fromDomain(SeatAssignmentJpa jpa, SeatAssignment domain) {
        Objects.requireNonNull(jpa, "SeatAssignmentJpa entity cannot be null");
        Objects.requireNonNull(domain, "SeatAssignment domain entity cannot be null");

        jpa.setSeatNo(domain.getSeatNo().value());
        jpa.setStatus(domain.getStatus().name());
    }
}
