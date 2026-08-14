package cm.jemil.agency.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import cm.jemil.agency.application.inbound.usecase.GetAllAgenciesUseCaseImpl;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyName;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.LicenceNumber;
import cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1;
import cm.jemil.shared.utils.CreatedAt;
import cm.jemil.shared.utils.PageData;
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
        var a = Agency.of(new AgencyName("A"), new PhoneNumber("1", "2"), new LicenceNumber("yuyugy"));
        var b = Agency.of(new AgencyName("B"), new PhoneNumber("1", "2"), new LicenceNumber("gfh"));
        when(agencyRepository.loadAllAgency(any(), any()))
                .thenReturn(new PageData<>(2, List.of(viewOf(a), viewOf(b)), 1, 10, 0));

        var result = service.execute(new GetAllAgenciesUseCaseImpl.Query(null, 0, 10));

        assertThat(result.allAgencies()).hasSize(2);
        assertThat(result.allAgencies().get(0).name().value()).isEqualTo("A");
        assertThat(result.allAgencies().get(1).name().value()).isEqualTo("B");
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.pageNumber()).isZero();
    }

    @Test
    void shouldReturnEmptyListWhenNoAgencies() {
        when(agencyRepository.loadAllAgency(any(), any())).thenReturn(new PageData<>(0, List.of(), 0, 10, 0));

        var result = service.execute(new GetAllAgenciesUseCaseImpl.Query(null, 0, 10));

        assertThat(result.allAgencies()).isEmpty();
    }

    @Test
    void shouldFilterAgenciesByCityIgnoringCase() {
        var d = Agency.of(
                new AgencyName("Douala Agency"), new PhoneNumber("237", "1"), new LicenceNumber("vhjvhjvhjv"));
        var y = Agency.of(new AgencyName("Yaounde Agency"), new PhoneNumber("237", "2"), new LicenceNumber("fiuuiiu"));
        when(agencyRepository.loadAllAgency(any(), any()))
                .thenReturn(new PageData<>(2, List.of(viewOf(d), viewOf(y)), 1, 10, 0));

        var result = service.execute(new GetAllAgenciesUseCaseImpl.Query("douala", 0, 10));

        assertThat(result.allAgencies()).hasSize(2);
    }

    private AgencyView1 viewOf(Agency agency) {
        return new AgencyView1(
                agency.getId(),
                agency.getName(),
                agency.getPhoneNumber(),
                agency.getStatus(),
                List.of(),
                agency.getLicenseNumber(),
                new CreatedAt());
    }
}
