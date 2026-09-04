package cm.jemil.booking.domain.trip;

import java.util.List;

/** Port for reading the active schedule templates that drive trip generation. */
public interface ScheduleTemplateRepository {
    List<ActiveScheduleTemplate> findActiveTemplates();
}
