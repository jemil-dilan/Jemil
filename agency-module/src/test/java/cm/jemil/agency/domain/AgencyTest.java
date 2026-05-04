package cm.jemil.agency.domain;

import cm.jemil.agency.domain.event.AgencyRegisteredEvent;
import cm.jemil.agency.domain.exception.AgencyDomainException;
import cm.jemil.agency.domain.model.Agency;
import cm.jemil.agency.domain.model.AgencyStatus;
import cm.jemil.agency.domain.model.Route;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires de l'Aggregate Agency.
 *
 * <p>Ces tests ne démarrent PAS Spring. Ils testent uniquement la logique métier.
 * Ultra rapides (< 10ms par test). C'est la force d'un domaine sans dépendances.
 *
 * <p>Convention de nommage :
 * - @DisplayName décrit le comportement en langage naturel
 * - Les méthodes de test suivent : should_[résultat]_when_[condition]
 */
@DisplayName("Agency Aggregate")
class AgencyTest {

    @Nested
    @DisplayName("Enregistrement d'une agence")
    class Registration {

        @Test
        @DisplayName("devrait créer une agence active avec un ID généré")
        void should_create_active_agency_with_generated_id() {
            Agency agency = Agency.register("Global Voyages", "Douala", "+237655000000");

            assertThat(agency.getId()).isNotNull();
            assertThat(agency.getName()).isEqualTo("Global Voyages");
            assertThat(agency.getCity()).isEqualTo("Douala");
            assertThat(agency.getContactPhone()).isEqualTo("+237655000000");
            assertThat(agency.getStatus()).isEqualTo(AgencyStatus.ACTIVE);
            assertThat(agency.getRoutes()).isEmpty();
        }

        @Test
        @DisplayName("devrait émettre un événement AgencyRegisteredEvent")
        void should_emit_agency_registered_event() {
            Agency agency = Agency.register("Trésor Voyages", "Bafoussam", null);

            List<Object> events = agency.pullDomainEvents();

            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(AgencyRegisteredEvent.class);

            AgencyRegisteredEvent event = (AgencyRegisteredEvent) events.get(0);
            assertThat(event.agencyName()).isEqualTo("Trésor Voyages");
            assertThat(event.city()).isEqualTo("Bafoussam");
            assertThat(event.occurredAt()).isNotNull();
        }

        @Test
        @DisplayName("devrait vider les événements après pullDomainEvents()")
        void should_clear_events_after_pull() {
            Agency agency = Agency.register("Blue Bird", "Yaoundé", null);

            agency.pullDomainEvents(); // premier pull
            List<Object> secondPull = agency.pullDomainEvents();

            assertThat(secondPull).isEmpty();
        }

        @Test
        @DisplayName("devrait refuser un nom null ou vide")
        void should_reject_blank_name() {
            assertThatThrownBy(() -> Agency.register("", "Douala", null))
                    .isInstanceOf(AgencyDomainException.class)
                    .hasMessageContaining("nom");

            assertThatThrownBy(() -> Agency.register(null, "Douala", null))
                    .isInstanceOf(AgencyDomainException.class);
        }

        @Test
        @DisplayName("devrait refuser un nom trop court (< 2 caractères)")
        void should_reject_too_short_name() {
            assertThatThrownBy(() -> Agency.register("A", "Douala", null))
                    .isInstanceOf(AgencyDomainException.class)
                    .hasMessageContaining("2");
        }

        @Test
        @DisplayName("devrait refuser une ville null ou vide")
        void should_reject_blank_city() {
            assertThatThrownBy(() -> Agency.register("Global Voyages", "", null))
                    .isInstanceOf(AgencyDomainException.class)
                    .hasMessageContaining("ville");
        }
    }

    @Nested
    @DisplayName("Ajout de routes")
    class RouteAddition {

        @Test
        @DisplayName("devrait ajouter une route valide à une agence active")
        void should_add_valid_route_to_active_agency() {
            Agency agency = Agency.register("Global Voyages", "Douala", null);

            Route route = agency.addRoute("Douala", "Yaoundé", 50);

            assertThat(agency.getRoutes()).hasSize(1);
            assertThat(route.getOrigin()).isEqualTo("Douala");
            assertThat(route.getDestination()).isEqualTo("Yaoundé");
            assertThat(route.getTotalSeats()).isEqualTo(50);
            assertThat(route.isActive()).isTrue();
        }

        @Test
        @DisplayName("devrait refuser d'ajouter une route à une agence suspendue")
        void should_reject_route_for_suspended_agency() {
            Agency agency = Agency.register("Global Voyages", "Douala", null);
            agency.pullDomainEvents(); // vider les events
            agency.suspend("Non-conformité");

            assertThatThrownBy(() -> agency.addRoute("Douala", "Yaoundé", 50))
                    .isInstanceOf(AgencyDomainException.class)
                    .hasMessageContaining("suspendue");
        }

        @Test
        @DisplayName("devrait refuser départ = destination")
        void should_reject_same_origin_destination() {
            Agency agency = Agency.register("Global Voyages", "Douala", null);

            assertThatThrownBy(() -> agency.addRoute("Douala", "Douala", 50))
                    .isInstanceOf(AgencyDomainException.class)
                    .hasMessageContaining("identiques");
        }

        @Test
        @DisplayName("devrait refuser 0 place ou plus de 100 places")
        void should_reject_invalid_seats() {
            Agency agency = Agency.register("Global Voyages", "Douala", null);

            assertThatThrownBy(() -> agency.addRoute("Douala", "Yaoundé", 0))
                    .isInstanceOf(AgencyDomainException.class);

            assertThatThrownBy(() -> agency.addRoute("Douala", "Yaoundé", 101))
                    .isInstanceOf(AgencyDomainException.class);
        }
    }

    @Nested
    @DisplayName("Suspension et réactivation")
    class Suspension {

        @Test
        @DisplayName("devrait suspendre une agence active")
        void should_suspend_active_agency() {
            Agency agency = Agency.register("Global Voyages", "Douala", null);
            agency.pullDomainEvents();

            agency.suspend("Problème de sécurité");

            assertThat(agency.getStatus()).isEqualTo(AgencyStatus.SUSPENDED);
        }

        @Test
        @DisplayName("devrait refuser de suspendre une agence déjà suspendue")
        void should_reject_double_suspension() {
            Agency agency = Agency.register("Global Voyages", "Douala", null);
            agency.pullDomainEvents();
            agency.suspend("Raison 1");

            assertThatThrownBy(() -> agency.suspend("Raison 2"))
                    .isInstanceOf(AgencyDomainException.class)
                    .hasMessageContaining("déjà suspendue");
        }

        @Test
        @DisplayName("devrait réactiver une agence suspendue")
        void should_reactivate_suspended_agency() {
            Agency agency = Agency.register("Global Voyages", "Douala", null);
            agency.pullDomainEvents();
            agency.suspend("Raison temporaire");

            agency.reactivate();

            assertThat(agency.getStatus()).isEqualTo(AgencyStatus.ACTIVE);
        }
    }
}
