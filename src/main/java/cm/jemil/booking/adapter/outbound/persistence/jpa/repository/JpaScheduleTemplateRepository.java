package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.adapter.outbound.persistence.jpa.repository.mapper.ScheduleTemplateJpaMapper;
import cm.jemil.booking.domain.trip.ActiveScheduleTemplate;
import cm.jemil.booking.domain.trip.ScheduleTemplate;
import cm.jemil.booking.domain.trip.ScheduleTemplateId;
import cm.jemil.booking.domain.trip.ScheduleTemplateRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaScheduleTemplateRepository implements ScheduleTemplateRepository {

    private final ScheduleTemplateSpringRepository scheduleTemplateSpringRepository;
    private final ScheduleTemplateJpaMapper jpaMapper;

    @Override
    public void save(ScheduleTemplate scheduleTemplate) {
        scheduleTemplateSpringRepository.save(jpaMapper.toJpa(scheduleTemplate));
    }

    @Override
    public Optional<ScheduleTemplate> findById(ScheduleTemplateId id) {
        return scheduleTemplateSpringRepository.findById(id.value()).map(jpaMapper::toDomain);
    }

    @Override
    public List<ScheduleTemplate> findAllActive() {
        return scheduleTemplateSpringRepository.findByActive(true).stream()
                .map(jpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<ScheduleTemplate> findByRouteId(UUID routeId) {
        return scheduleTemplateSpringRepository.findByRouteId(routeId).stream()
                .map(jpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<ScheduleTemplate> findByBusId(cm.jemil.booking.domain.bus.BusId busId) {
        return scheduleTemplateSpringRepository.findByBusId(busId.value()).stream()
                .map(jpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<ScheduleTemplate> findTemplatesToGenerate(LocalDate date) {
        // For now, return all active templates
        return findAllActive();
    }

    @Override
    public List<ActiveScheduleTemplate> findActiveTemplates() {
        // Legacy method for backward compatibility
        return scheduleTemplateSpringRepository.findActiveTemplates();
    }
}
