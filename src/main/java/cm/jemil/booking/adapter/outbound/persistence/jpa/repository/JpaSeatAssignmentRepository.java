package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.SeatAssignmentJpa;
import cm.jemil.booking.adapter.outbound.persistence.jpa.repository.mapper.SeatAssignmentJpaMapper;
import cm.jemil.booking.domain.booking.BookingId;
import cm.jemil.booking.domain.booking.SeatAssignment;
import cm.jemil.booking.domain.booking.SeatAssignmentId;
import cm.jemil.booking.domain.booking.SeatAssignmentRepository;
import cm.jemil.booking.domain.seat.SeatNumber;
import cm.jemil.booking.domain.seat.SeatStatus;
import cm.jemil.booking.domain.trip.TripId;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaSeatAssignmentRepository implements SeatAssignmentRepository {

    private final SeatAssignmentSpringRepository seatAssignmentSpringRepository;
    private final SeatAssignmentJpaMapper jpaMapper;

    @Override
    public SeatAssignment save(SeatAssignment seatAssignment) {
        SeatAssignmentJpa jpa = jpaMapper.toJpa(seatAssignment);
        SeatAssignmentJpa saved = seatAssignmentSpringRepository.save(jpa);
        return jpaMapper.toDomain(saved);
    }

    @Override
    public List<SeatAssignment> saveAll(List<SeatAssignment> seatAssignments) {
        List<SeatAssignmentJpa> jpas =
                seatAssignments.stream().map(jpaMapper::toJpa).toList();
        List<SeatAssignmentJpa> saved = seatAssignmentSpringRepository.saveAll(jpas);
        return saved.stream().map(jpaMapper::toDomain).toList();
    }

    @Override
    public Optional<SeatAssignment> findById(SeatAssignmentId id) {
        return seatAssignmentSpringRepository.findById(id.value()).map(jpaMapper::toDomain);
    }

    @Override
    public List<SeatAssignment> findByBookingId(BookingId bookingId) {
        return seatAssignmentSpringRepository.findByBookingId(bookingId.value()).stream()
                .map(jpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<SeatAssignment> findByTripId(TripId tripId) {
        return seatAssignmentSpringRepository.findByTripId(tripId.value()).stream()
                .map(jpaMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<SeatAssignment> findByTripIdAndSeatNo(TripId tripId, SeatNumber seatNo) {
        return seatAssignmentSpringRepository
                .findByTripIdAndSeatNo(tripId.value(), seatNo.value())
                .map(jpaMapper::toDomain);
    }

    @Override
    public List<Integer> findTakenSeatNosByTripId(TripId tripId) {
        return seatAssignmentSpringRepository.findTakenSeatNosByTripId(tripId.value());
    }

    @Override
    public boolean isSeatAvailable(TripId tripId, SeatNumber seatNo) {
        return !seatAssignmentSpringRepository.existsByTripIdAndSeatNoAndStatusIn(
                tripId.value(), seatNo.value(), List.of("HELD", "SOLD"));
    }

    @Override
    public List<SeatAssignment> findByTripIdAndStatus(TripId tripId, SeatStatus status) {
        return seatAssignmentSpringRepository.findByTripIdAndStatus(tripId.value(), status.name()).stream()
                .map(jpaMapper::toDomain)
                .toList();
    }
}
