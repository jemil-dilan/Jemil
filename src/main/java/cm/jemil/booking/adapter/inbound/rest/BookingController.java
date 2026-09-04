package cm.jemil.booking.adapter.inbound.rest;

import cm.jemil.booking.application.inbound.usecase.PlaceBookingHoldUseCase;
import cm.jemil.booking.application.inbound.usecase.SearchTripsUseCase;
import cm.jemil.generated.booking.adapter.rest.inbound.api.BookingApi;
import cm.jemil.generated.booking.adapter.rest.inbound.api.TripApi;
import cm.jemil.generated.booking.adapter.rest.inbound.dto.CreateBookingHoldDTO;
import cm.jemil.generated.booking.adapter.rest.inbound.dto.TripSearchResponseDTO;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookingController implements TripApi, BookingApi {

    private final SearchTripsUseCase searchTripsUseCase;
    private final PlaceBookingHoldUseCase placeBookingHoldUseCase;
    private final BookingRestMapper restMapper;

    @Override
    public ResponseEntity<TripSearchResponseDTO> searchTrips(
            UUID originCityId, UUID destinationCityId, LocalDate serviceDate) {
        var trips = searchTripsUseCase.execute(originCityId, destinationCityId, serviceDate);
        return ResponseEntity.ok(restMapper.toTripSearchResponse(trips));
    }

    @Override
    public ResponseEntity<cm.jemil.generated.booking.adapter.rest.inbound.dto.BookingHoldResponseDTO> createBookingHold(
            CreateBookingHoldDTO createBookingHoldDTO) {
        var result = placeBookingHoldUseCase.execute(new PlaceBookingHoldUseCase.Command(
                createBookingHoldDTO.getTripId(),
                createBookingHoldDTO.getSeatNos(),
                createBookingHoldDTO.getPassengerName(),
                createBookingHoldDTO.getPassengerMsisdn()));
        return ResponseEntity.status(HttpStatus.CREATED).body(restMapper.toBookingHoldResponse(result));
    }
}
