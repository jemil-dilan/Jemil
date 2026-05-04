package cm.jemil.agency.domain.model;

import cm.jemil.agency.domain.event.AgencyRegisteredEvent;
import cm.jemil.agency.domain.exception.AgencyDomainException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate Root : Agency.
 *
 * <p>En DDD, un Aggregate Root est l'entité principale qui protège l'intégrité
 * du domaine. Toute modification passe par ses méthodes — jamais directement
 * sur les champs. Les règles métier vivent ICI, pas dans les services.
 *
 * <p>Règles métier encapsulées :
 * - Une agence doit avoir un nom et une ville
 * - Une agence inactive ne peut pas ajouter de routes
 * - Les événements domaine sont collectés pour être publiés après la persistance
 */
public class Agency {

    private final AgencyId id;
    private String name;
    private String city;
    private String contactPhone;
    private AgencyStatus status;
    private final List<Route> routes;

    // Les événements domaine sont collectés ici.
    // Le repository les publie APRÈS avoir sauvegardé l'agence.
    // Ainsi on ne publie jamais un événement pour quelque chose qui n'a pas été sauvegardé.
    private final List<Object> domainEvents = new ArrayList<>();

    // ── Constructeur privé — on passe par les factory methods ──
    private Agency(AgencyId id, String name, String city, String contactPhone) {
        this.id           = id;
        this.name         = name;
        this.city         = city;
        this.contactPhone = contactPhone;
        this.status       = AgencyStatus.ACTIVE;
        this.routes       = new ArrayList<>();
    }

    /**
     * Factory method : enregistre une nouvelle agence.
     * C'est le seul moyen de créer une Agency valide.
     */
    public static Agency register(String name, String city, String contactPhone) {
        validateName(name);
        validateCity(city);

        Agency agency = new Agency(AgencyId.generate(), name, city, contactPhone);

        // On collecte l'événement — il sera publié après la sauvegarde
        agency.domainEvents.add(new AgencyRegisteredEvent(agency.id, name, city));

        return agency;
    }

    /**
     * Reconstitution depuis la persistance.
     * Cette factory method est utilisée par le repository pour reconstruire
     * une Agency depuis la base de données. Elle ne génère pas d'événements.
     */
    public static Agency reconstitute(
            AgencyId id, String name, String city, String contactPhone,
            AgencyStatus status, List<Route> routes) {
        Agency agency = new Agency(id, name, city, contactPhone);
        agency.status = status;
        agency.routes.addAll(routes);
        return agency;
    }

    /** Ajoute une route à cette agence. */
    public Route addRoute(String origin, String destination, int totalSeats) {
        if (this.status == AgencyStatus.SUSPENDED) {
            throw new AgencyDomainException(
                    "L'agence " + name + " est suspendue. Impossible d'ajouter une route.");
        }

        Route route = Route.create(RouteId.generate(), this.id, origin, destination, totalSeats);
        this.routes.add(route);
        return route;
    }

    /** Suspend l'agence (ex: non-conformité). */
    public void suspend(String reason) {
        if (this.status == AgencyStatus.SUSPENDED) {
            throw new AgencyDomainException("L'agence est déjà suspendue.");
        }
        this.status = AgencyStatus.SUSPENDED;
    }

    /** Réactive une agence suspendue. */
    public void reactivate() {
        this.status = AgencyStatus.ACTIVE;
    }

    // ── Accesseurs (lecture seule — pas de setters publics) ────
    public AgencyId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public AgencyStatus getStatus() {
        return status;
    }

    public List<Route> getRoutes() {
        return Collections.unmodifiableList(routes); // pas de modification externe
    }

    /** Retourne les événements collectés et vide la liste. */
    public List<Object> pullDomainEvents() {
        List<Object> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }

    // ── Validation privée — règles métier ─────────────────────
    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new AgencyDomainException("Le nom de l'agence est obligatoire.");
        }
        if (name.length() < 2 || name.length() > 100) {
            throw new AgencyDomainException("Le nom doit contenir entre 2 et 100 caractères.");
        }
    }

    private static void validateCity(String city) {
        if (city == null || city.isBlank()) {
            throw new AgencyDomainException("La ville de l'agence est obligatoire.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Agency agency)) return false;
        return Objects.equals(id, agency.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Agency{id=" + id + ", name='" + name + "', city='" + city + "', status=" + status + "}";
    }
}
