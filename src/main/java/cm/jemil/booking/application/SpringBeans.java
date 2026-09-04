package cm.jemil.booking.application;

import cm.jemil.booking.application.inbound.usecase.PlaceBookingHoldUseCase;
import cm.jemil.booking.application.inbound.usecase.PlaceBookingHoldUseCaseImpl;
import cm.jemil.booking.application.inbound.usecase.SearchTripsUseCase;
import cm.jemil.booking.application.inbound.usecase.SearchTripsUseCaseImpl;
import cm.jemil.booking.domain.trip.TripRepository;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("bookingApplicationBeans")
@RequiredArgsConstructor
public class SpringBeans {

    @Bean("bookingClock")
    Clock bookingClock() {
        return Clock.systemUTC();
    }

    @Bean
    SearchTripsUseCase searchTripsUseCase(
            TripRepository tripRepository,
            @org.springframework.beans.factory.annotation.Qualifier("bookingClock") Clock bookingClock) {
        return new SearchTripsUseCaseImpl(tripRepository, bookingClock);
    }

    @Bean
    PlaceBookingHoldUseCase placeBookingHoldUseCase(
            TripRepository tripRepository, cm.jemil.booking.inventory.SeatHoldService seatHoldService) {
        return new PlaceBookingHoldUseCaseImpl(tripRepository, seatHoldService);
    }
}
