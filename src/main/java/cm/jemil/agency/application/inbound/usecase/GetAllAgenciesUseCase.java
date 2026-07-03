package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.views.AgencyView.AgencyView1;
import java.util.List;
import org.jspecify.annotations.Nullable;

public interface GetAllAgenciesUseCase {
    List<AgencyView1> execute(@Nullable String city);

    List<AgencyView1> execute(@Nullable String city, int page, int size);
}
