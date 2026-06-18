package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.shared.utils.Address;
import cm.jemil.shared.utils.PhoneNumber;

public interface RegisterAgencyUseCase {
    AgencyId register(String name, Address address, PhoneNumber phoneNumber);
}
