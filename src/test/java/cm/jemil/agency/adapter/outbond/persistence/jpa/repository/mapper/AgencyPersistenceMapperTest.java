package cm.jemil.agency.adapter.outbond.persistence.jpa.repository.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.agency.adapter.outbond.persistence.jpa.entity.AgencyJpaEntity;
import cm.jemil.agency.adapter.outbond.persistence.jpa.entity.RouteJpaEntity;
import cm.jemil.agency.adapter.outbond.persistence.jpa.entity.ScheduleJpaEntity;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyStatus;
import cm.jemil.agency.domain.agency.Route;
import cm.jemil.agency.domain.agency.RouteId;
import cm.jemil.agency.domain.agency.Schedule;
import cm.jemil.agency.domain.agency.ScheduleId;
import cm.jemil.shared.utils.Address;
import cm.jemil.shared.utils.PhoneNumber;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AgencyPersistenceMapperTest {

    private final AgencyPersistenceMapper mapper = new AgencyPersistenceMapperImpl();

    @Test
    void shouldMapDomainToJpaEntity() {
        var agency = Agency.register(
                "Global Voyages", new Address("Douala", "Bonanjo"), new PhoneNumber("237", "653492410"));

        var entity = mapper.toJpaEntity(agency);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(agency.getId().value());
        assertThat(entity.getName()).isEqualTo("Global Voyages");
        assertThat(entity.getCity()).isEqualTo("Douala");
        assertThat(entity.getDistrict()).isEqualTo("Bonanjo");
        assertThat(entity.getCountryCode()).isEqualTo("237");
        assertThat(entity.getPhoneNumber()).isEqualTo("653492410");
        assertThat(entity.getStatus()).isEqualTo(AgencyStatus.ACTIVE);
    }

    @Test
    void shouldMapJpaToDomain() {
        var jpa = new AgencyJpaEntity();
        jpa.setId(UUID.randomUUID());
        jpa.setName("Global Voyages");
        jpa.setCity("Douala");
        jpa.setDistrict("Bonanjo");
        jpa.setCountryCode("237");
        jpa.setPhoneNumber("653492410");
        jpa.setStatus(AgencyStatus.ACTIVE);

        var agency = mapper.toDomain(jpa);

        assertThat(agency).isNotNull();
        assertThat(agency.getId().value()).isEqualTo(jpa.getId());
        assertThat(agency.getName()).isEqualTo("Global Voyages");
        assertThat(agency.getAddress().city()).isEqualTo("Douala");
        assertThat(agency.getPhoneNumber().fullNumber()).isEqualTo("+237653492410");
        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.ACTIVE);
    }

    @Test
    void shouldMapRouteToJpaEntity() {
        var routeId = RouteId.generate();
        var route = new Route(routeId, "Douala", "Yaoundé", 5000, new ArrayList<>());

        var entity = mapper.toJpaEntity(route);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(routeId.value());
        assertThat(entity.getDeparture()).isEqualTo("Douala");
        assertThat(entity.getArrival()).isEqualTo("Yaoundé");
        assertThat(entity.getPrice()).isEqualTo(5000);
    }

    @Test
    void shouldMapScheduleToJpaEntity() {
        var scheduleId = ScheduleId.generate();
        var departureTime = LocalDateTime.of(2026, 6, 15, 8, 0);
        var schedule = new Schedule(scheduleId, departureTime, 50, 50);

        var entity = mapper.toJpaEntity(schedule);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(scheduleId.value());
        assertThat(entity.getDepartureTime()).isEqualTo(departureTime);
        assertThat(entity.getTotalSeats()).isEqualTo(50);
        assertThat(entity.getAvailableSeats()).isEqualTo(50);
    }

    @Test
    void shouldMapNullJpaToNull() {
        assertThat(mapper.toDomain((AgencyJpaEntity) null)).isNull();
    }

    @Test
    void shouldMapNullRouteToNull() {
        assertThat(mapper.toDomain((RouteJpaEntity) null)).isNull();
    }

    @Test
    void shouldMapNullScheduleToNull() {
        assertThat(mapper.toDomain((ScheduleJpaEntity) null)).isNull();
    }

    @Test
    void shouldMapAgencyIdToUuid() {
        var uuid = UUID.randomUUID();
        var id = new AgencyId(uuid);

        assertThat(mapper.map(id)).isEqualTo(uuid);
    }

    @Test
    void shouldMapNullAgencyIdToNull() {
        assertThat(mapper.map((AgencyId) null)).isNull();
    }
}
