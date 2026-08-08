package cm.jemil.agency.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import cm.jemil.agency.application.inbound.usecase.GetAllAgenciesUseCaseImpl;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyName;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.CommissionRate;
import cm.jemil.agency.domain.agency.LicenceNumber;
import cm.jemil.shared.utils.PhoneNumber;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GetAllAgenciesUseCaseImplTest {

    @Mock
    private AgencyRepository agencyRepository;

    private GetAllAgenciesUseCaseImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new GetAllAgenciesUseCaseImpl(agencyRepository);
    }

    @Test
    void shouldReturnAllAgencies() {
        var a = Agency.of(new AgencyName("A"), new PhoneNumber("1", "2"), new LicenceNumber("yuyugy"), new CommissionRate(4.0));
        var b = Agency.of(new AgencyName("B"), new PhoneNumber("1", "2"), new LicenceNumber("gfh"), new CommissionRate(3.0));
        var agencies = List.of(
                new cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1(
                        a.getId(),
                        a.getName().value(),
                        a.getPhoneNumber(),
                        a.getStatus(),
                        List.of(),
                        a.getLicenseNumber().value(),
                        a.getCommissionRateValue(),
                        a.getCreatedAt()),
                new cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1(
                        b.getId(),
                        b.getName().value(),
                        b.getPhoneNumber(),
                        b.getStatus(),
                        List.of(),
                        b.getLicenseNumber().value(),
                        b.getCommissionRateValue(),
                        b.getCreatedAt()));
        when(agencyRepository.getAllAgencyView1(any())).thenReturn(agencies);

        var result = service.execute(null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("A");
        assertThat(result.get(1).name()).isEqualTo("B");
    }

    @Test
    void shouldReturnEmptyListWhenNoAgencies() {
        when(agencyRepository.getAllAgencyView1(any())).thenReturn(List.of());

        var result = service.execute(null);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFilterAgenciesByCityIgnoringCase() {
        var d = Agency.of(
                new AgencyName("Douala Agency"), new PhoneNumber("237", "1"), new LicenceNumber("vhjvhjvhjv"), new CommissionRate(6.0));
        var y = Agency.of(
                new AgencyName("Yaounde Agency"), new PhoneNumber("237", "2"), new LicenceNumber("fiuuiiu"), new CommissionRate(1.0));
        var agencies2 = List.of(
                new cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1(
                        d.getId(),
                        d.getName().value(),
                        d.getPhoneNumber(),
                        d.getStatus(),
                        List.of(),
                        d.getLicenseNumber().value(),
                        d.getCommissionRateValue(),
                        d.getCreatedAt()),
                new cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1(
                        y.getId(),
                        y.getName().value(),
                        y.getPhoneNumber(),
                        y.getStatus(),
                        List.of(),
                        y.getLicenseNumber().value(),
                        y.getCommissionRateValue(),
                        y.getCreatedAt()));
        when(agencyRepository.getAllAgencyView1(any())).thenReturn(agencies2);

        var result = service.execute("douala");

        assertThat(result).hasSize(2);
    }

    @Test
    void shouldReturnOnlyActiveAgencies() {
        var activeAgg =
                Agency.of(new AgencyName("Active"), new PhoneNumber("237", "1"), new LicenceNumber("vhjvhjvhjv"), new CommissionRate(6.0));
        var suspendedAgg =
                Agency.of(new AgencyName("Suspended"), new PhoneNumber("237", "2"), new LicenceNumber("fiuuiiu"), new CommissionRate(1.0));
        suspendedAgg.suspend();
        var active = new cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1(
                activeAgg.getId(),
                activeAgg.getName().value(),
                activeAgg.getPhoneNumber(),
                activeAgg.getStatus(),
                List.of(),
                activeAgg.getLicenseNumber().value(),
                activeAgg.getCommissionRateValue(),
                activeAgg.getCreatedAt());
        var suspended = new cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1(
                suspendedAgg.getId(),
                suspendedAgg.getName().value(),
                suspendedAgg.getPhoneNumber(),
                suspendedAgg.getStatus(),
                List.of(),
                suspendedAgg.getLicenseNumber().value(),
                suspendedAgg.getCommissionRateValue(),
                suspendedAgg.getCreatedAt());
        when(agencyRepository.getAllAgencyView1(any())).thenReturn(List.of(active, suspended));

        var result = service.execute(null);

        assertThat(result).containsExactly(active);
    }
}
