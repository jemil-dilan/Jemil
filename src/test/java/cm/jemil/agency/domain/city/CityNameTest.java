package cm.jemil.agency.domain.city;

import static org.assertj.core.api.Assertions.assertThat;

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
}
