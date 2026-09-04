package cm.jemil.agency.adapter.inbound.rest;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.agency.application.inbound.usecase.GetAllAgenciesUseCaseImpl;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyName;
import cm.jemil.agency.domain.agency.LicenceNumber;
import cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1;
import cm.jemil.generated.agency.adapter.rest.inbound.dto.AddRouteDTO;
import cm.jemil.shared.utils.CreatedAt;
import cm.jemil.shared.utils.PhoneNumber;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AgencyRestMapperTest {

    private final AgencyRestMapper mapper = new AgencyRestMapperImpl();

    @Test
    void shouldMapAgencyToDto() {
        var agencyAgg = Agency.of(
                new AgencyName("Global Voyages"), new PhoneNumber("237", "653492410"), new LicenceNumber("sjoiaj"));
        var agency = new AgencyView1(
                agencyAgg.getId(),
                agencyAgg.getName(),
                agencyAgg.getPhoneNumber(),
                agencyAgg.getStatus(),
                List.of(),
                agencyAgg.getLicenseNumber(),
                CreatedAt.now());

        var dto = mapper.toPaginationDTO(agency);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(agency.id().value());
        assertThat(dto.getName()).isEqualTo("Global Voyages");
        assertThat(dto.getPhoneNumber()).isNotNull();
        assertThat(dto.getPhoneNumber().getCountryCode()).isEqualTo("237");
    }

    @Test
    void shouldMapPaginationResponseToAgencyPaginationDto() {
        var agencyAgg = Agency.of(
                new AgencyName("Global Voyages"), new PhoneNumber("237", "653492410"), new LicenceNumber("sjoiaj"));
        var agency = new AgencyView1(
                agencyAgg.getId(),
                agencyAgg.getName(),
                agencyAgg.getPhoneNumber(),
                agencyAgg.getStatus(),
                List.of(),
                agencyAgg.getLicenseNumber(),
                CreatedAt.now());

        var dto = mapper.toDTO(new GetAllAgenciesUseCaseImpl.Response(List.of(agency), 1, 1, 10, 0));

        assertThat(dto).isNotNull();
        assertThat(dto.getContent()).hasSize(1);
        assertThat(dto.getContent().get(0).getId()).isEqualTo(agency.id().value());
        assertThat(dto.getTotalElements()).isEqualTo(1);
        assertThat(dto.getTotalPages()).isEqualTo(1);
        assertThat(dto.getSize()).isEqualTo(10);
        assertThat(dto.getNumber()).isZero();
    }

    @Test
    void shouldMapAddRouteDtoToCommand() {
        var agencyId = UUID.randomUUID();
        var origin = UUID.randomUUID();
        var destination = UUID.randomUUID();
        var dto = new AddRouteDTO()
                .originCityId(origin)
                .destinationCityId(destination)
                .price(5000.0)
                .totalSeats(40);

        var command = mapper.toCommand(agencyId, dto);

        assertThat(command.agencyId()).isEqualTo(agencyId);
        assertThat(command.originCityId()).isEqualTo(origin);
        assertThat(command.destinationCityId()).isEqualTo(destination);
        assertThat(command.priceXaf()).isEqualTo(5000);
        assertThat(command.totalSeats()).isEqualTo(40);
    }
}
