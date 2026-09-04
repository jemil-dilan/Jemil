package cm.jemil.booking.adapter.outbound.persistence.jpa.repository;

import cm.jemil.booking.domain.trip.ActiveScheduleTemplate;
import cm.jemil.booking.domain.trip.ScheduleTemplateRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaScheduleTemplateRepository implements ScheduleTemplateRepository {

    private final ScheduleTemplateSpringRepository scheduleTemplateSpringRepository;

    @Override
    public List<ActiveScheduleTemplate> findActiveTemplates() {
        return scheduleTemplateSpringRepository.findActiveTemplates();
    }
}
