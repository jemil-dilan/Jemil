package cm.jemil.booking.adapter.outbound.persistence.jpa.repository.mapper;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.SeatAssignmentJpa;
import cm.jemil.booking.domain.booking.BookingId;
import cm.jemil.booking.domain.booking.SeatAssignment;
import cm.jemil.booking.domain.booking.SeatAssignmentId;
import cm.jemil.booking.domain.seat.SeatNumber;
import cm.jemil.booking.domain.seat.SeatStatus;
import cm.jemil.booking.domain.trip.TripId;
import cm.jemil.shared.utils.CreatedAt;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper between SeatAssignment domain entity and SeatAssignmentJpa entity.
 */
@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SeatAssignmentJpaMapper {

    SeatAssignmentJpa toJpa(SeatAssignment domain);

    default SeatAssignment toDomain(SeatAssignmentJpa jpa) {
        Objects.requireNonNull(jpa, "SeatAssignmentJpa entity cannot be null");

        return SeatAssignment.reconstitute(
                new SeatAssignmentId(jpa.getId()),
                new TripId(jpa.getTripId()),
                new SeatNumber(jpa.getSeatNo()),
                new BookingId(jpa.getBookingId()),
                SeatStatus.valueOf(jpa.getStatus()),
                CreatedAt.reconstitute(jpa.getCreatedAt().toLocalDateTime()));
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tripId", ignore = true)
    @Mapping(target = "bookingId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void fromDomain(@MappingTarget SeatAssignmentJpa jpa, SeatAssignment domain);

    default UUID map(SeatAssignmentId value) {
        return value == null ? null : value.value();
    }

    default UUID map(TripId value) {
        return value == null ? null : value.value();
    }

    default int map(SeatNumber value) {
        return value == null ? 0 : value.value();
    }

    default UUID map(BookingId value) {
        return value == null ? null : value.value();
    }

    default OffsetDateTime map(CreatedAt value) {
        return value == null ? null : value.value().atOffset(ZoneOffset.UTC);
    }
}
