package cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.AgencyJpa;
import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.RouteJpa;
import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.ScheduleJpa;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyStatus;
import cm.jemil.agency.domain.agency.Route;
import cm.jemil.agency.domain.agency.RouteId;
import cm.jemil.agency.domain.agency.Schedule;
import cm.jemil.agency.domain.agency.ScheduleId;
import cm.jemil.shared.utils.PhoneNumber;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AgencyJpaMapperTest {

    private final AgencyJpaMapper mapper = new AgencyJpaMapperImpl();

    @Test
    void shouldMapDomainToJpa() {
        var agency = Agency.of("Global Voyages", new PhoneNumber("237", "653492410"), "sjoiaj", 3.0);

        var entity = mapper.toJpa(agency);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(agency.getId().value());
        assertThat(entity.getName()).isEqualTo("Global Voyages");
        assertThat(entity.getPhoneCountryCode()).isEqualTo("237");
        assertThat(entity.getPhoneNumber()).isEqualTo("653492410");
        assertThat(entity.getStatus()).isEqualTo(AgencyStatus.ACTIVE);
    }

    @Test
    void shouldMapJpaToAgencyView1() {
        var jpa = new AgencyJpa();
        jpa.setId(UUID.randomUUID());
        jpa.setName("Global Voyages");
        jpa.setPhoneCountryCode("237");
        jpa.setPhoneNumber("653492410");
        jpa.setStatus(AgencyStatus.ACTIVE);
        jpa.setCreatedAt(LocalDateTime.now());

        var agency = mapper.toAgencyView1(jpa);

        assertThat(agency).isNotNull();
        assertThat(agency.id().value()).isEqualTo(jpa.getId());
        assertThat(agency.name()).isEqualTo("Global Voyages");
        assertThat(agency.phoneNumber().fullNumber()).isEqualTo("+237653492410");
        assertThat(agency.status()).isEqualTo(AgencyStatus.ACTIVE);
    }

    @Test
    void shouldMapRouteToJpa() {
        var routeId = RouteId.generate();
        var route = new Route(routeId, "Douala", "Yaoundé", 5000, new ArrayList<>());

        var entity = mapper.toJpa(route);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(routeId.value());
        assertThat(entity.getDeparture()).isEqualTo("Douala");
        assertThat(entity.getArrival()).isEqualTo("Yaoundé");
        assertThat(entity.getPrice()).isEqualTo(5000);
    }

    @Test
    void shouldMapScheduleToJpa() {
        var scheduleId = ScheduleId.generate();
        var departureTime = LocalDateTime.of(2026, 6, 15, 8, 0);
        var schedule = new Schedule(scheduleId, departureTime, 50, 50);

        var entity = mapper.toJpa(schedule);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(scheduleId.value());
        assertThat(entity.getDepartureTime()).isEqualTo(departureTime);
        assertThat(entity.getTotalSeats()).isEqualTo(50);
        assertThat(entity.getAvailableSeats()).isEqualTo(50);
    }

    @Test
    void shouldMapNullJpaToNull() {
        assertThat(mapper.toAgencyView1((AgencyJpa) null)).isNull();
    }

    @Test
    void shouldMapNullRouteToNull() {
        assertThat(mapper.toAgencyView1((RouteJpa) null)).isNull();
    }

    @Test
    void shouldMapNullScheduleToNull() {
        assertThat(mapper.toAgencyView1((ScheduleJpa) null)).isNull();
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
