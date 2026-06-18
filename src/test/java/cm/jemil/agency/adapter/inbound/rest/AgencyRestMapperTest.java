package cm.jemil.agency.adapter.inbound.rest;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.shared.utils.Address;
import cm.jemil.shared.utils.PhoneNumber;
import org.junit.jupiter.api.Test;

class AgencyRestMapperTest {

    private final AgencyRestMapper mapper = new AgencyRestMapperImpl();

    @Test
    void shouldMapAgencyToDto() {
        var agency = Agency.register(
                "Global Voyages", new Address("Douala", "Bonanjo"), new PhoneNumber("237", "653492410"));

        var dto = mapper.toDto(agency);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(agency.getId().value());
        assertThat(dto.getName()).isEqualTo("Global Voyages");
        assertThat(dto.getAddress()).isNotNull();
        assertThat(dto.getAddress().getCity()).isEqualTo("Douala");
        assertThat(dto.getPhoneNumber()).isNotNull();
        assertThat(dto.getPhoneNumber().getCountryCode()).isEqualTo("237");
    }

    @Test
    void shouldMapToAddress() {
        var dto = new cm.jemil.generated.agency.adapter.rest.inbound.dto.RegisterAgencyDTO();
        var addressDto = new cm.jemil.generated.agency.adapter.rest.inbound.dto.AddressDTO();
        addressDto.setCity("Douala");
        addressDto.setDistrict("Bonanjo");
        dto.setAddress(addressDto);

        var address = mapper.toAddress(dto);

        assertThat(address).isNotNull();
        assertThat(address.city()).isEqualTo("Douala");
        assertThat(address.district()).isEqualTo("Bonanjo");
    }

    @Test
    void shouldMapToPhoneNumber() {
        var dto = new cm.jemil.generated.agency.adapter.rest.inbound.dto.RegisterAgencyDTO();
        var phoneDto = new cm.jemil.generated.agency.adapter.rest.inbound.dto.PhoneNumberDTO();
        phoneDto.setCountryCode("237");
        phoneDto.setNumber("653492410");
        dto.setPhoneNumber(phoneDto);

        var phone = mapper.toPhoneNumber(dto);

        assertThat(phone).isNotNull();
        assertThat(phone.countryCode()).isEqualTo("237");
        assertThat(phone.number()).isEqualTo("653492410");
    }

    @Test
    void shouldReturnNullAddressWhenDtoHasNullAddress() {
        var dto = new cm.jemil.generated.agency.adapter.rest.inbound.dto.RegisterAgencyDTO();

        assertThat(mapper.toAddress(dto)).isNull();
    }

    @Test
    void shouldReturnNullPhoneWhenDtoHasNullPhone() {
        var dto = new cm.jemil.generated.agency.adapter.rest.inbound.dto.RegisterAgencyDTO();

        assertThat(mapper.toPhoneNumber(dto)).isNull();
    }
}
