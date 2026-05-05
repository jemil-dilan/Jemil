package cm.jemil.agency.domain.port.in;

import cm.jemil.agency.domain.model.Agency;

/**
 * Port entrant : ce que le monde extérieur peut DEMANDER au module Agency.
 *
 * <p>En architecture hexagonale :
 * - Les PORTS sont des interfaces définies dans le DOMAINE
 * - Les ADAPTERS sont les implémentations dans l'INFRASTRUCTURE
 *
 * <p>Ce port définit le contrat. Le controller REST l'appelle.
 * La couche application l'implémente.
 * Le domaine ne sait pas que HTTP existe.
 */
public interface RegisterAgencyUseCase {

    /**
     * Enregistre une nouvelle agence partenaire JEMIL.
     *
     * @param command les données de l'agence à enregistrer
     * @return l'agence créée avec son identifiant généré
     */
    Agency registerAgency(RegisterAgencyCommand command);

    /**
     * Command Object : encapsule les données d'entrée du use case.
     * Immuable par construction (record).
     */
    record RegisterAgencyCommand(String name, String city, String contactPhone) {

        public RegisterAgencyCommand {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Le nom est obligatoire.");
            }
            if (city == null || city.isBlank()) {
                throw new IllegalArgumentException("La ville est obligatoire.");
            }
        }
    }
}
