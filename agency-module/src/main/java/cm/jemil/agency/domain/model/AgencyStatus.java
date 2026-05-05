package cm.jemil.agency.domain.model;

/** Statuts possibles d'une agence. */
public enum AgencyStatus {
    ACTIVE, // agence opérationnelle
    SUSPENDED, // suspendue (non-conformité, problème sécurité)
    INACTIVE // désactivée définitivement
}
