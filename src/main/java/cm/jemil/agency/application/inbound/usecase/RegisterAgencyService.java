package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyRegisteredEvent;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.shared.outbox.OutboxEventPublisher;
import cm.jemil.shared.utils.Address;
import cm.jemil.shared.utils.PhoneNumber;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegisterAgencyService implements RegisterAgencyUseCase {
    private final AgencyRepository agencyRepository;
    private final OutboxEventPublisher eventPublisher;

    @Override
    public AgencyId register(String name, Address address, PhoneNumber phoneNumber) {
        Agency agency = Agency.register(name, address, phoneNumber);
        agencyRepository.save(agency);
        eventPublisher.publish(AgencyRegisteredEvent.of(agency.getId(), agency.getName()));
        return agency.getId();
    }
}
