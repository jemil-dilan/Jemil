package cm.nyi.agency.domain.branch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cm.nyi.agency.domain.exception.AgencyErrorCode;
import cm.nyi.shared.exception.DomainException;
import org.junit.jupiter.api.Test;

class BranchAddressTest {

    @Test
    void shouldCreateBranchAddress() {
        var address = new BranchAddress("123 Rue Principale");
        assertThat(address.value()).isEqualTo("123 Rue Principale");
    }

    @Test
    void shouldRejectBlankAddress() {
        assertThatThrownBy(() -> new BranchAddress(" "))
                .isInstanceOf(DomainException.class)
                .extracting("code")
                .isEqualTo(AgencyErrorCode.BRANCH_400_003.getCode());
    }
}
