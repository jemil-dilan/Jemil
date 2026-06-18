package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.Agency;
import java.util.List;

public interface GetAllAgenciesUseCase {
    List<Agency> execute(String city);
}
