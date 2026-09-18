package cm.jemil.booking.adapter.outbound.persistence.jpa.repository.mapper;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.BookingJpa;
import cm.jemil.booking.domain.booking.Booking;
import cm.jemil.booking.domain.booking.BookingId;
import cm.jemil.booking.domain.booking.BookingReference;
import cm.jemil.booking.domain.booking.Channel;
import cm.jemil.booking.domain.booking.PassengerInfo;
import cm.jemil.booking.domain.trip.TripId;
import cm.jemil.shared.utils.CreatedAt;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Mapper between Booking domain entity and BookingJpa entity.
 */
@Component
public class BookingJpaMapper {

    public BookingJpa toJpa(Booking domain) {
        Objects.requireNonNull(domain, "Booking domain entity cannot be null");

        BookingJpa jpa = new BookingJpa();
        jpa.setId(domain.getId().value());
        jpa.setRef(domain.getRef().value());
        jpa.setTripId(domain.getTripId().value());
        jpa.setChannel(domain.getChannel().name());
        jpa.setPassengerName(domain.getPassengerName());
        jpa.setPassengerMsisdn(domain.getPassengerMsisdn());
        jpa.setAmountXaf(domain.getAmountXaf());
        jpa.setStatus(domain.getStatus().name());
        jpa.setHoldExpiresAt(domain.getHoldExpiresAt());
        jpa.setCreatedAt(domain.getCreatedAt().value().atOffset(java.time.ZoneOffset.UTC));
        return jpa;
    }

    public Booking toDomain(BookingJpa jpa) {
        Objects.requireNonNull(jpa, "BookingJpa entity cannot be null");

        // PhoneNumber requires country code and number
        // For now, use "237" (Cameroon) as default country code
        var phoneNumber = jpa.getPassengerMsisdn() != null
                ? new cm.jemil.shared.utils.PhoneNumber("237", jpa.getPassengerMsisdn())
                : new cm.jemil.shared.utils.PhoneNumber("237", "");
        var passengerInfo = new PassengerInfo(jpa.getPassengerName(), phoneNumber);

        return Booking.reconstitute(
                new BookingId(jpa.getId()),
                new BookingReference(jpa.getRef()),
                new TripId(jpa.getTripId()),
                Channel.valueOf(jpa.getChannel()),
                passengerInfo,
                jpa.getAmountXaf(),
                cm.jemil.booking.domain.booking.BookingStatus.valueOf(jpa.getStatus()),
                jpa.getHoldExpiresAt(),
                CreatedAt.reconstitute(jpa.getCreatedAt().toLocalDateTime()));
    }

    public void fromDomain(BookingJpa jpa, Booking domain) {
        Objects.requireNonNull(jpa, "BookingJpa entity cannot be null");
        Objects.requireNonNull(domain, "Booking domain entity cannot be null");

        jpa.setRef(domain.getRef().value());
        jpa.setTripId(domain.getTripId().value());
        jpa.setChannel(domain.getChannel().name());
        jpa.setPassengerName(domain.getPassengerName());
        jpa.setPassengerMsisdn(domain.getPassengerMsisdn());
        jpa.setAmountXaf(domain.getAmountXaf());
        jpa.setStatus(domain.getStatus().name());
        jpa.setHoldExpiresAt(domain.getHoldExpiresAt());
    }
}
