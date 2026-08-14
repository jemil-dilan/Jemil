package cm.jemil.agency.config;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@Validated
@Component
@ConfigurationProperties(prefix = "agency")
class AgencyApplicationProperties {

    private CommissionRate commissionRate = new CommissionRate();

    @Getter
    @Setter
    static class CommissionRate {
        private BigDecimal value = BigDecimal.ZERO;
    }
}
