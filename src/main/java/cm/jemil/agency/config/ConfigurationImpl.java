package cm.jemil.agency.config;

import cm.jemil.agency.application.starter.Configuration;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ConfigurationImpl implements Configuration {
    private final AgencyApplicationProperties agencyApplicationProperties;

    @Override
    public BigDecimal getCommissionRate() {
        return agencyApplicationProperties.getCommissionRate().getValue();
    }
}
