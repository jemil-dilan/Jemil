package cm.nyi.booking.adapter.inbound.rest;

import cm.nyi.booking.application.inbound.usecase.PlaceBookingHoldUseCase;
import cm.nyi.booking.domain.trip.TripSearchView;
import cm.nyi.booking.domain.trip.TripSeatMap;
import cm.nyi.generated.booking.adapter.rest.inbound.dto.CreateBookingHoldDTO;
import cm.nyi.generated.booking.adapter.rest.inbound.dto.CreationResponseDTO;
import cm.nyi.generated.booking.adapter.rest.inbound.dto.TripDTO;
import cm.nyi.generated.booking.adapter.rest.inbound.dto.TripSearchResponseDTO;
import cm.nyi.generated.booking.adapter.rest.inbound.dto.TripSeatMapDTO;
import java.util.UUID;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingRestMapper {

    @Mapping(target = "passengerMsisdn", source = "passengerMsisdn")
    @Mapping(target = "passengerName", source = "passengerName")
    @Mapping(target = "seatNos", source = "seatNos")
    @Mapping(target = "tripId", source = "tripId")
    PlaceBookingHoldUseCase.Command toCommand(CreateBookingHoldDTO createBookingHoldDTO);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "newId", source = "id")
    CreationResponseDTO toCreationResponse(UUID id);

    default TripSearchResponseDTO toTripSearchResponse(java.util.List<TripSearchView> trips) {
        var response = new TripSearchResponseDTO();
        var content = trips.stream().map(this::toTripDto).toList();
        response.setContent(content);
        response.setTotalElements(content.size());
        return response;
    }

    TripDTO toTripDto(TripSearchView view);

    TripSeatMapDTO toTripSeatMap(TripSeatMap seatMap);
}
