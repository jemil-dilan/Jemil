package cm.nyi.booking.adapter.outbound.persistence.jpa.repository;

import cm.nyi.booking.adapter.outbound.persistence.jpa.repository.mapper.BookingJpaMapper;
import cm.nyi.booking.domain.booking.Booking;
import cm.nyi.booking.domain.booking.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaBookingRepository implements BookingRepository {

    private final BookingSpringRepository bookingSpringRepository;
    private final BookingJpaMapper jpaMapper;

    @Override
    public void save(Booking booking) {
        bookingSpringRepository.save(jpaMapper.toJpa(booking));
    }
}
