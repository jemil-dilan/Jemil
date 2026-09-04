package cm.jemil.booking.adapter.inbound.rest;

import cm.jemil.booking.domain.trip.TripSearchView;
import cm.jemil.booking.inventory.BookingHoldResult;
import cm.jemil.generated.booking.adapter.rest.inbound.dto.BookingHoldResponseDTO;
import cm.jemil.generated.booking.adapter.rest.inbound.dto.TripDTO;
import cm.jemil.generated.booking.adapter.rest.inbound.dto.TripSearchResponseDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingRestMapper {

    default TripSearchResponseDTO toTripSearchResponse(java.util.List<TripSearchView> trips) {
        var response = new TripSearchResponseDTO();
        var content = trips.stream().map(this::toTripDto).toList();
        response.setContent(content);
        response.setTotalElements(content.size());
        return response;
    }

    default TripDTO toTripDto(TripSearchView view) {
        var dto = new TripDTO();
        dto.setId(view.id());
        dto.setAgencyId(view.agencyId());
        dto.setAgencyName(view.agencyName());
        dto.setRouteId(view.routeId());
        dto.setOriginCityId(view.originCityId());
        dto.setDestinationCityId(view.destinationCityId());
        dto.setDepartureAt(view.departureAt());
        dto.setServiceDate(view.serviceDate());
        dto.setPriceXaf(view.priceXaf());
        dto.setTravelClass(view.travelClass());
        dto.setStatus(view.status());
        dto.setSeatsTotal(view.seatsTotal());
        dto.setSeatsRemaining(view.seatsRemaining());
        return dto;
    }

    default BookingHoldResponseDTO toBookingHoldResponse(BookingHoldResult result) {
        var dto = new BookingHoldResponseDTO();
        dto.setId(result.id());
        dto.setRef(result.ref());
        dto.setTripId(result.tripId());
        dto.setHoldExpiresAt(result.holdExpiresAt());
        dto.setAmountXaf(result.amountXaf());
        dto.setSeatNos(result.seatNos());
        return dto;
    }
}
