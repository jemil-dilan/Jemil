package cm.jemil.agency.domain.branch;

import static org.assertj.core.api.Assertions.assertThat;

import cm.jemil.agency.domain.city.CityId;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AgencyBranchTest {

    @Test
    void shouldCreateBranch() {
        var cityId = new CityId(UUID.randomUUID());
        var branch = AgencyBranch.of("Gare de Douala", "123 Rue Principale", cityId);

        assertThat(branch.getId()).isNotNull();
        assertThat(branch.getName()).isEqualTo("Gare de Douala");
        assertThat(branch.getAddress()).isEqualTo("123 Rue Principale");
        assertThat(branch.isActive()).isTrue();
        assertThat(branch.getCityId()).isEqualTo(cityId);
    }

    @Test
    void shouldHaveUniqueIds() {
        var cityId = new CityId(UUID.randomUUID());
        var branch1 = AgencyBranch.of("Gare A", "Adresse 1", cityId);
        var branch2 = AgencyBranch.of("Gare B", "Adresse 2", cityId);

        assertThat(branch1.getId()).isNotEqualTo(branch2.getId());
    }
}
