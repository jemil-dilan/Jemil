package cm.jemil.agency.adapter.outbond.persistence.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cm.jemil.agency.adapter.outbond.persistence.jpa.entity.AgencyJpaEntity;
import cm.jemil.agency.adapter.outbond.persistence.jpa.repository.mapper.AgencyPersistenceMapper;
import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.shared.utils.Address;
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
    private AgencyJpaRepository jpaRepository;

    @Mock
    private AgencyPersistenceMapper mapper;

    private JpaAgencyRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new JpaAgencyRepository(jpaRepository, mapper);
    }

    @Test
    void shouldSaveAgency() {
        var agency = Agency.register("Test", new Address("X", "Y"), new PhoneNumber("1", "2"));
        var jpaEntity = new AgencyJpaEntity();
        when(mapper.toJpaEntity(agency)).thenReturn(jpaEntity);

        repository.save(agency);

        verify(jpaRepository).save(jpaEntity);
    }

    @Test
    void shouldFindByIdWhenPresent() {
        var uuid = UUID.randomUUID();
        var id = new AgencyId(uuid);
        var jpaEntity = new AgencyJpaEntity();
        var agency = Agency.register("Test", new Address("X", "Y"), new PhoneNumber("1", "2"));
        when(jpaRepository.findById(uuid)).thenReturn(Optional.of(jpaEntity));
        when(mapper.toDomain(jpaEntity)).thenReturn(agency);

        var result = repository.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test");
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        var uuid = UUID.randomUUID();
        var id = new AgencyId(uuid);
        when(jpaRepository.findById(uuid)).thenReturn(Optional.empty());

        var result = repository.findById(id);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindAllAgencies() {
        var jpaEntity = new AgencyJpaEntity();
        var agency = Agency.register("Test", new Address("X", "Y"), new PhoneNumber("1", "2"));
        when(jpaRepository.findAll()).thenReturn(List.of(jpaEntity));
        when(mapper.toDomain(jpaEntity)).thenReturn(agency);

        var result = repository.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Test");
    }

    @Test
    void shouldReturnEmptyListWhenNoneFound() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        var result = repository.findAll();

        assertThat(result).isEmpty();
    }
}
