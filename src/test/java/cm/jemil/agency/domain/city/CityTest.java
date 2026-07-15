package cm.jemil.agency.domain.city;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CityTest {

    @Test
    void shouldCreateCity() {
        var city = City.of(new CityName("Douala"), new CityRegion("Littoral"));

        assertThat(city.getId()).isNotNull();
        assertThat(city.getName().value()).isEqualTo("Douala");
        assertThat(city.getRegion().value()).isEqualTo("Littoral");
    }

    @Test
    void shouldHaveUniqueIds() {
        var city1 = City.of(new CityName("Douala"), new CityRegion("Littoral"));
        var city2 = City.of(new CityName("Yaoundé"), new CityRegion("Centre"));

        assertThat(city1.getId()).isNotEqualTo(city2.getId());
    }
}
