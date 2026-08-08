package cm.jemil.agency.application.inbound.usecase;

import cm.jemil.agency.domain.agency.Agency;
import cm.jemil.agency.domain.agency.AgencyId;
import cm.jemil.agency.domain.agency.AgencyName;
import cm.jemil.agency.domain.agency.AgencyRegisteredEvent;
import cm.jemil.agency.domain.agency.AgencyRepository;
import cm.jemil.agency.domain.agency.CommissionRate;
import cm.jemil.agency.domain.agency.LicenceNumber;
import cm.jemil.shared.outbox.OutboxEventPublisher;
import cm.jemil.shared.utils.PhoneNumber;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegisterAgencyUseCaseImpl implements RegisterAgencyUseCase {
    private final AgencyRepository agencyRepository;
    private final OutboxEventPublisher eventPublisher;

    @Override
    public AgencyId execute(Command command) {
        Agency agency = Agency.of(
                new AgencyName(command.name),
                command.phoneNumber,
                new LicenceNumber(command.licenseNumber),
                new CommissionRate(command.commissionRate));
        agencyRepository.insert(agency);
        eventPublisher.publish(AgencyRegisteredEvent.of(agency.getId(), agency.getName()));
        return agency.getId();
    }

    public record Command(String name, PhoneNumber phoneNumber, String licenseNumber, double commissionRate) {}
}

