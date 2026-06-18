package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.AgencyStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAllAgenciesService implements GetAllAgenciesUseCase {
    private final AgencyRepository agencyRepository;

    @Override
    public List<Agency> execute(String city) {
        return agencyRepository.findAll().stream()
                .filter(agency -> agency.getStatus() == AgencyStatus.ACTIVE)
                .filter(agency -> hasCity(agency, city))
                .toList();
    }

    private boolean hasCity(Agency agency, String city) {
        if (city == null || city.isBlank()) {
            return true;
        }
        return agency.getAddress() != null
                && agency.getAddress().city() != null
                && agency.getAddress().city().equalsIgnoreCase(city);
    }
}
