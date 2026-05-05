package cm.jemil.agency.application.usecase;

import cm.jemil.agency.domain.model.Agency;
import cm.jemil.agency.domain.port.in.RegisterAgencyUseCase;
import cm.jemil.agency.domain.port.out.AgencyEventPublisher;
import cm.jemil.agency.domain.port.out.AgencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application Service : implémente RegisterAgencyUseCase.
 *
 * <p>Ce service orchestre le use case :
 * 1. Crée l'agence via le domaine (règles métier)
 * 2. Persiste via le port sortant (repository)
 * 3. Publie les événements domaine collectés
 *
 * <p>Il ne contient PAS de logique métier — c'est le domaine qui la contient.
 * Il est le chef d'orchestre, pas l'acteur.
 */
@Service
@Transactional
public class RegisterAgencyService implements RegisterAgencyUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterAgencyService.class);

    // Dépendances vers les PORTS (interfaces) — jamais vers les implémentations
    private final AgencyRepository agencyRepository;
    private final AgencyEventPublisher eventPublisher;

    public RegisterAgencyService(AgencyRepository agencyRepository, AgencyEventPublisher eventPublisher) {
        this.agencyRepository = agencyRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Agency registerAgency(RegisterAgencyCommand command) {
        log.info("Enregistrement d'une nouvelle agence : {} - {}", command.name(), command.city());

        // 1. Le domaine crée l'agence et valide les règles métier
        Agency agency = Agency.register(command.name(), command.city(), command.contactPhone());

        // 2. On collecte les événements AVANT de sauvegarder
        var events = agency.pullDomainEvents();

        // 3. On persiste
        Agency savedAgency = agencyRepository.save(agency);

        // 4. On publie les événements APRÈS la persistance réussie
        // Ainsi on ne publie jamais un événement pour une agence non sauvegardée
        events.forEach(eventPublisher::publish);

        log.info("Agence enregistrée avec succès : {}", savedAgency.getId());

        return savedAgency;
    }
}
