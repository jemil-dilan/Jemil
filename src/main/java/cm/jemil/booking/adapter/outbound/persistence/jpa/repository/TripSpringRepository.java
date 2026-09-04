package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbound.persistence.jpa.entity.TripJpa;
import cm.jemil.booking.domain.trip.TripSearchView;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TripSpringRepository extends JpaRepository<TripJpa, UUID> {

    @Query("""
            SELECT new cm.jemil.booking.domain.trip.TripSearchView(
                t.id,
                t.agencyId,
                a.name,
                t.routeId,
                r.departureId,
                r.arrivalId,
                t.departureAt,
                t.serviceDate,
                t.priceXaf,
                t.travelClass,
                t.status,
                t.seatsTotal,
                t.seatsTotal - CAST((
                    SELECT COUNT(sa.id)
                    FROM SeatAssignmentJpa sa
                    WHERE sa.tripId = t.id AND sa.status IN ('HELD', 'SOLD')
                ) AS Integer)
            )
            FROM TripJpa t, RouteJpa r, AgencyJpa a
            WHERE t.routeId = r.id
              AND t.agencyId = a.id
              AND r.departureId = :originCityId
              AND r.arrivalId = :destinationCityId
              AND t.serviceDate = :serviceDate
              AND t.status = 'OPEN'
              AND t.departureAt > :earliestDeparture
              AND a.status = cm.jemil.agency.domain.agency.AgencyStatus.ACTIVE
            ORDER BY t.departureAt ASC
            """)
    List<TripSearchView> searchTrips(
            @Param("originCityId") UUID originCityId,
            @Param("destinationCityId") UUID destinationCityId,
            @Param("serviceDate") LocalDate serviceDate,
            @Param("earliestDeparture") OffsetDateTime earliestDeparture);
}
