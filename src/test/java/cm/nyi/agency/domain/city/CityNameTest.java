package cm.nyi.agency.domain.city;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cm.nyi.agency.domain.exception.AgencyErrorCode;
import cm.nyi.shared.exception.DomainException;
import org.junit.jupiter.api.Test;

class CityNameTest {

    @Test
    void shouldCreateCityName() {
        var name = new CityName("Douala");
        assertThat(name.value()).isEqualTo("Douala");
    }

    @Test
    void shouldBeEqualForSameValue() {
        var name1 = new CityName("Douala");
        var name2 = new CityName("Douala");
        assertThat(name1).isEqualTo(name2);
    }

    @Test
    void shouldRejectBlankName() {
        assertThatThrownBy(() -> new CityName("   "))
                .isInstanceOf(DomainException.class)
                .extracting("code")
                .isEqualTo(AgencyErrorCode.CITY_400_001.getCode());
    }
}
