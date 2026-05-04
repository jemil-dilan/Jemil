package cm.jemil.agency.domain.port.out;

import cm.jemil.agency.domain.model.Agency;
import cm.jemil.agency.domain.model.AgencyId;
import java.util.List;
import java.util.Optional;

/**
 * Port sortant : ce dont le domaine a besoin de l'extérieur (la DB).
 *
 * <p>C'est une interface définie dans le DOMAINE.
 * L'implémentation (JPA, MongoDB, etc.) est dans l'INFRASTRUCTURE.
 * Le domaine ne sait pas que PostgreSQL existe.
 *
 * <p>Quand tu veux changer de base de données, tu crées
 * une nouvelle implémentation de ce port. Le domaine ne change pas.
 */
public interface AgencyRepository {

    /** Sauvegarde une agence (création ou mise à jour). */
    Agency save(Agency agency);

    /** Trouve une agence par son identifiant. */
    Optional<Agency> findById(AgencyId agencyId);

    /** Retourne toutes les agences actives. */
    List<Agency> findAllActive();

    /** Trouve les agences d'une ville donnée. */
    List<Agency> findByCity(String city);

    /** Vérifie qu'une agence existe. */
    boolean existsById(AgencyId agencyId);
}
