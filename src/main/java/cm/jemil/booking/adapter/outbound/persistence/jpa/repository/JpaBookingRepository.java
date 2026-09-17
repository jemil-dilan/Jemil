package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.BookingJpa;
import cm.jemil.booking.adapter.outbound.persistence.jpa.repository.mapper.BookingJpaMapper;
import cm.jemil.booking.domain.booking.Booking;
import cm.jemil.booking.domain.booking.BookingId;
import cm.jemil.booking.domain.booking.BookingReference;
import cm.jemil.booking.domain.booking.BookingRepository;
import cm.jemil.booking.domain.seat.SeatNumber;
import cm.jemil.booking.domain.trip.TripId;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaBookingRepository implements BookingRepository {

    private final BookingSpringRepository bookingSpringRepository;
    private final BookingJpaMapper jpaMapper;

    @Override
    public Booking save(Booking booking) {
        BookingJpa jpa = jpaMapper.toJpa(booking);
        BookingJpa saved = bookingSpringRepository.save(jpa);
        return jpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Booking> findById(BookingId id) {
        return bookingSpringRepository.findById(id.value()).map(jpaMapper::toDomain);
    }

    @Override
    public Optional<Booking> findByReference(BookingReference ref) {
        return bookingSpringRepository.findByRef(ref.value()).map(jpaMapper::toDomain);
    }

    @Override
    public List<Booking> findByTripId(TripId tripId) {
        return bookingSpringRepository.findByTripId(tripId.value()).stream()
                .map(jpaMapper::toDomain)
                .toList();
    }

    @Override
    public boolean isSeatAvailable(TripId tripId, SeatNumber seatNo) {
        // Check if there's any booking with HELD or SOLD status for this trip and seat
        return !bookingSpringRepository.existsByTripIdAndSeatNoAndStatusIn(
                tripId.value(), seatNo.value(), List.of("HELD", "SOLD"));
    }

    @Override
    public List<Booking> findExpiredBookings() {
        var now = OffsetDateTime.now();
        return bookingSpringRepository.findByStatusAndHoldExpiresAtBefore("HELD", now).stream()
                .map(jpaMapper::toDomain)
                .toList();
    }
}
