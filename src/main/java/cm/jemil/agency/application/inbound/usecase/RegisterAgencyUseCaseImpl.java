package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyRegisteredEvent;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.shared.outbox.OutboxEventPublisher;
import cm.jemil.shared.utils.PhoneNumber;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegisterAgencyUseCaseImpl implements RegisterAgencyUseCase {
    private final AgencyRepository agencyRepository;
    private final OutboxEventPublisher eventPublisher;

    @Override
    public AgencyId execute(Command command) {
        Agency agency = Agency.of(command.name, command.phoneNumber, command.licenseNumber, command.commissionRate);
        agencyRepository.insert(agency);
        eventPublisher.publish(AgencyRegisteredEvent.of(agency.getId(), agency.getName()));
        return agency.getId();
    }

    public record Command(String name, PhoneNumber phoneNumber, String licenseNumber, double commissionRate) {}
}
