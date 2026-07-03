package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.AgencyStatus;
import cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class GetAllAgenciesUseCaseImpl implements GetAllAgenciesUseCase {
    private final AgencyRepository agencyRepository;

    @Override
    public List<AgencyView1> execute(@Nullable String city) {
        return agencyRepository.getAllAgencyView1(city).stream()
                .filter(agency -> Objects.equals(agency.status(), AgencyStatus.ACTIVE))
                .toList();
    }

    @Override
    public List<AgencyView1> execute(@Nullable String city, int page, int size) {
        return agencyRepository.getAllAgencyView1(city, page, size);
    }
}
