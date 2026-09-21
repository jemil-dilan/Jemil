package cm.jemil.booking.domain.trip;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for schedule template domain operations.
 */
public interface ScheduleTemplateRepository {

    /**
     * Save a schedule template.
     */
    void save(ScheduleTemplate scheduleTemplate);

    /**
     * Find a schedule template by ID.
     */
    Optional<ScheduleTemplate> findById(ScheduleTemplateId id);

    /**
     * Find all active schedule templates.
     */
    List<ScheduleTemplate> findAllActive();

    /**
     * Find active schedule templates for trip generation (returns legacy ActiveScheduleTemplate records).
     * @deprecated Use findAllActive() and map to domain entities
     */
    @Deprecated
    List<ActiveScheduleTemplate> findActiveTemplates();

    /**
     * Find schedule templates by route ID.
     */
    List<ScheduleTemplate> findByRouteId(java.util.UUID routeId);

    /**
     * Find schedule templates by bus ID.
     */
    List<ScheduleTemplate> findByBusId(cm.jemil.booking.domain.bus.BusId busId);

    /**
     * Find schedule templates that should generate trips for a given date.
     */
    List<ScheduleTemplate> findTemplatesToGenerate(LocalDate date);
}
