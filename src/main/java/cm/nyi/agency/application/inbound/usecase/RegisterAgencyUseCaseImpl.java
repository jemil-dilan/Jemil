package cm.nyi.agency.application.inbound.usecase;

import cm.nyi.agency.domain.agency.Agency;
import cm.nyi.agency.domain.agency.AgencyId;
import cm.nyi.agency.domain.agency.AgencyName;
import cm.nyi.agency.domain.agency.AgencyRegisteredEvent;
import cm.nyi.agency.domain.agency.AgencyRepository;
import cm.nyi.agency.domain.agency.LicenceNumber;
import cm.nyi.shared.outbox.OutboxEventPublisher;
import cm.nyi.shared.utils.PhoneNumber;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegisterAgencyUseCaseImpl implements RegisterAgencyUseCase {
    private final AgencyRepository agencyRepository;
    private final OutboxEventPublisher eventPublisher;

    @Override
    public AgencyId execute(Command command) {
        Agency agency =
                Agency.of(new AgencyName(command.name), command.phoneNumber, new LicenceNumber(command.licenseNumber));
        agencyRepository.insert(agency);
        eventPublisher.publish(AgencyRegisteredEvent.of(agency.getId(), agency.getName()));
        return agency.getId();
    }

    public record Command(String name, PhoneNumber phoneNumber, String licenseNumber) {}
}
