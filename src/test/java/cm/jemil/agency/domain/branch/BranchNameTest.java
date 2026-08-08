package cm.jemil.agency.domain.branch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cm.jemil.agency.domain.exception.AgencyErrorCode;
import cm.jemil.shared.exception.DomainException;
import org.junit.jupiter.api.Test;

class BranchNameTest {

    @Test
    void shouldCreateBranchName() {
        var name = new BranchName("Gare de Douala");
        assertThat(name.value()).isEqualTo("Gare de Douala");
    }

    @Test
    void shouldRejectBlankName() {
        assertThatThrownBy(() -> new BranchName(" "))
                .isInstanceOf(DomainException.class)
                .extracting("code")
                .isEqualTo(AgencyErrorCode.BRANCH_400_001.getCode());
    }
}
