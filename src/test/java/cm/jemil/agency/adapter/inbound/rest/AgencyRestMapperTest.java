package cm.jemil.agency.adapter.inbound.rest;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyName;
import cm.jemil.agency.domain.agency.CommissionRate;
import cm.jemil.agency.domain.agency.LicenceNumber;
import cm.jemil.shared.utils.PhoneNumber;
import java.util.List;
import org.junit.jupiter.api.Test;

class AgencyRestMapperTest {

    private final AgencyRestMapper mapper = new AgencyRestMapperImpl();

    @Test
    void shouldMapAgencyToDto() {
        var agencyAgg = Agency.of(
                new AgencyName("Global Voyages"),
                new PhoneNumber("237", "653492410"),
                new LicenceNumber("sjoiaj"),
                new CommissionRate(3.0));
        var agency = new cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1(
                agencyAgg.getId(),
                agencyAgg.getName().value(),
                agencyAgg.getPhoneNumber(),
                agencyAgg.getStatus(),
                List.of(),
                agencyAgg.getLicenseNumber().value(),
                agencyAgg.getCommissionRateValue(),
                agencyAgg.getCreatedAt());

        var dto = mapper.toDto(agency);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(agency.id().value());
        assertThat(dto.getName()).isEqualTo("Global Voyages");
        assertThat(dto.getPhoneNumber()).isNotNull();
        assertThat(dto.getPhoneNumber().getCountryCode()).isEqualTo("237");
    }
}
