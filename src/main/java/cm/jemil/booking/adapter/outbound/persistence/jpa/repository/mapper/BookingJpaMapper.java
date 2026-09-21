package cm.jemil.booking.adapter.outbound.persistence.jpa.repository.mapper;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.BookingJpa;
import cm.jemil.booking.domain.booking.Booking;
import cm.jemil.booking.domain.booking.BookingId;
import cm.jemil.booking.domain.booking.BookingReference;
import cm.jemil.booking.domain.booking.BookingStatus;
import cm.jemil.booking.domain.booking.Channel;
import cm.jemil.booking.domain.booking.PassengerInfo;
import cm.jemil.booking.domain.trip.TripId;
import cm.jemil.shared.utils.CreatedAt;
import cm.jemil.shared.utils.PhoneNumber;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper between Booking domain entity and BookingJpa entity.
 */
@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingJpaMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "ref", source = "ref.value")
    @Mapping(target = "tripId", source = "tripId.value")
    @Mapping(target = "channel", source = "channel")
    @Mapping(target = "passengerName", source = "passengerInfo.name")
    @Mapping(target = "passengerMsisdn", source = "passengerInfo.phoneNumber.number")
    @Mapping(target = "amountXaf", source = "amountXaf")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "holdExpiresAt", source = "holdExpiresAt")
    @Mapping(target = "createdAt", source = "createdAt")
    BookingJpa toJpa(Booking domain);

    default Booking toDomain(BookingJpa jpa) {
        Objects.requireNonNull(jpa, "BookingJpa entity cannot be null");

        // PhoneNumber requires a non-blank number; fall back to the same placeholder used when placing a hold.
        var msisdn =
                jpa.getPassengerMsisdn() == null || jpa.getPassengerMsisdn().isBlank()
                        ? "0000000000"
                        : jpa.getPassengerMsisdn();
        var passengerInfo = new PassengerInfo(jpa.getPassengerName(), new PhoneNumber("237", msisdn));

        return Booking.reconstitute(
                new BookingId(jpa.getId()),
                new BookingReference(jpa.getRef()),
                new TripId(jpa.getTripId()),
                Channel.valueOf(jpa.getChannel()),
                passengerInfo,
                jpa.getAmountXaf(),
                BookingStatus.valueOf(jpa.getStatus()),
                jpa.getHoldExpiresAt(),
                CreatedAt.reconstitute(jpa.getCreatedAt().toLocalDateTime()));
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "ref", source = "ref.value")
    @Mapping(target = "tripId", source = "tripId.value")
    @Mapping(target = "channel", source = "channel")
    @Mapping(target = "passengerName", source = "passengerInfo.name")
    @Mapping(target = "passengerMsisdn", source = "passengerInfo.phoneNumber.number")
    @Mapping(target = "amountXaf", source = "amountXaf")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "holdExpiresAt", source = "holdExpiresAt")
    void fromDomain(@MappingTarget BookingJpa jpa, Booking domain);

    default OffsetDateTime map(CreatedAt createdAt) {
        return createdAt.value().atOffset(ZoneOffset.UTC);
    }
}
