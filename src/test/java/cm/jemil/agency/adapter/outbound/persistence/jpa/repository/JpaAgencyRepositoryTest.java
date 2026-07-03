package cm.jemil.agency.adapter.outbound.persistence.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.agency.adapter.outbound.persistence.jpa.entity.AgencyJpa;
import cm.jemil.agency.adapter.outbound.persistence.jpa.repository.mapper.AgencyJpaMapper;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.shared.exception.DomainException;
import cm.jemil.shared.utils.PhoneNumber;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class JpaAgencyRepositoryTest {

    @Mock
    private AgencySpringRepository jpaRepository;

    @Mock
    private AgencyJpaMapper mapper;

    private JpaAgencyRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new JpaAgencyRepository(jpaRepository, mapper);
    }

    @Test
    void shouldInsertAgency() {
        var agency = Agency.of("Test", new PhoneNumber("1", "2"), "boo", 2.0);
        var jpaEntity = new AgencyJpa();
        when(mapper.toJpa(agency)).thenReturn(jpaEntity);

        repository.insert(agency);

        verify(jpaRepository).save(jpaEntity);
    }

    @Test
    void shouldLoadByIdAgencyView1WhenPresent() {
        var uuid = UUID.randomUUID();
        var id = new AgencyId(uuid);
        var jpaEntity = new AgencyJpa();
        var agencyAgg = Agency.of("Test", new PhoneNumber("1", "2"), "boo", 2.0);
        var agency = new cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1(
                agencyAgg.getId(),
                agencyAgg.getName(),
                agencyAgg.getPhoneNumber(),
                agencyAgg.getStatus(),
                List.of(),
                agencyAgg.getLicenseNumber(),
                agencyAgg.getCommissionRate(),
                agencyAgg.getCreatedAt());
        when(jpaRepository.findById(uuid)).thenReturn(Optional.of(jpaEntity));
        when(mapper.toAgencyView1(jpaEntity)).thenReturn(agency);

        var result = repository.loadByIdAgencyView1(id);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Test");
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        var uuid = UUID.randomUUID();
        var id = new AgencyId(uuid);
        when(jpaRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> repository.loadByIdAgencyView1(id))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void shouldGetAllAgencyView1Agencies() {
        var jpaEntity = new AgencyJpa();
        var agencyAgg2 = Agency.of("Test", new PhoneNumber("1", "2"), "boo", 2.0);
        var agency2 = new cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1(
                agencyAgg2.getId(),
                agencyAgg2.getName(),
                agencyAgg2.getPhoneNumber(),
                agencyAgg2.getStatus(),
                List.of(),
                agencyAgg2.getLicenseNumber(),
                agencyAgg2.getCommissionRate(),
                agencyAgg2.getCreatedAt());
        when(jpaRepository.findAll()).thenReturn(List.of(jpaEntity));
        when(mapper.toAgencyView1(jpaEntity)).thenReturn(agency2);

        var result = repository.getAllAgencyView1();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Test");
    }

    @Test
    void shouldReturnEmptyListWhenNoneFound() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        var result = repository.getAllAgencyView1();

        assertThat(result).isEmpty();
    }
}
