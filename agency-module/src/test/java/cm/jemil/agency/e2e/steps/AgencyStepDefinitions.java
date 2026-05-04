package cm.jemil.agency.e2e.steps;

import cm.jemil.agency.e2e.config.CucumberSpringConfiguration;
import io.cucumber.java.Before;
import io.cucumber.java.fr.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Step Definitions Cucumber pour les scénarios d'agence.
 *
 * <p>Chaque méthode correspond à une ligne Gherkin du fichier .feature.
 * RestAssured fait les appels HTTP réels vers le serveur Spring Boot de test.
 *
 * <p>Pattern : les steps partagent l'état via des champs d'instance.
 * Cucumber crée une nouvelle instance pour chaque scénario (isolation garantie).
 */
public class AgencyStepDefinitions extends CucumberSpringConfiguration {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // État partagé entre les steps d'un même scénario
    private Response lastResponse;
    private String lastCreatedAgencyId;

    @Before
    public void setUp() {
        // Configure RestAssured pour pointer vers le serveur de test
        RestAssured.baseURI = "http://localhost";
        RestAssured.port    = port;

        // Nettoie la DB avant chaque scénario pour l'isolation
        jdbcTemplate.execute("DELETE FROM routes");
        jdbcTemplate.execute("DELETE FROM agencies");
    }

    // ── Steps "Etant donné" (Given) ───────────────────────────

    @Etantdonné("que le système JEMIL est opérationnel")
    public void le_systeme_est_operationnel() {
        // Le serveur démarre avec @SpringBootTest — rien à faire ici
        // Ce step documente le contexte pour le lecteur humain
    }

    @Etantdonné("qu'une agence {string} existe dans la ville {string}")
    public void une_agence_existe(String nom, String ville) {
        // Crée l'agence via l'API pour que les tests soient réalistes
        Map<String, String> body = Map.of(
                "name", nom,
                "city", ville,
                "contactPhone", "+237600000000");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("/api/v1/agencies");

        assertThat(response.statusCode())
                .as("L'agence de contexte devrait être créée avec succès")
                .isEqualTo(201);
    }

    // ── Steps "Quand" (When) ──────────────────────────────────

    @Quand("j'enregistre une agence avec les informations suivantes:")
    public void j_enregistre_une_agence(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        Map<String, String> body = Map.of(
                "name",         data.getOrDefault("nom", ""),
                "city",         data.getOrDefault("ville", ""),
                "contactPhone", data.getOrDefault("telephone", ""));

        lastResponse = given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("/api/v1/agencies");

        // Sauvegarde l'ID si la création a réussi
        if (lastResponse.statusCode() == 201) {
            lastCreatedAgencyId = lastResponse.jsonPath().getString("id");
        }
    }

    @Quand("je récupère l'agence par son identifiant")
    public void je_recupere_lagence_par_son_identifiant() {
        assertThat(lastCreatedAgencyId)
                .as("Un ID d'agence doit exister avant de faire une recherche par ID")
                .isNotNull();

        lastResponse = given()
                .get("/api/v1/agencies/" + lastCreatedAgencyId);
    }

    @Quand("je récupère les agences de la ville {string}")
    public void je_recupere_les_agences_par_ville(String ville) {
        lastResponse = given()
                .queryParam("city", ville)
                .get("/api/v1/agencies");
    }

    // ── Steps "Alors" (Then) ──────────────────────────────────

    @Alors("l'agence est créée avec le statut {string}")
    public void lagence_est_creee_avec_le_statut(String statut) {
        String actualStatus = lastResponse.jsonPath().getString("status");
        assertThat(actualStatus)
                .as("Le statut de l'agence créée devrait être " + statut)
                .isEqualTo(statut);
    }

    @Alors("l'agence possède un identifiant unique")
    public void lagence_possede_un_identifiant() {
        String id = lastResponse.jsonPath().getString("id");
        assertThat(id)
                .as("L'agence doit avoir un identifiant UUID")
                .isNotNull()
                .isNotBlank()
                .matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @Alors("je reçois une réponse HTTP {int}")
    public void je_recois_une_reponse_http(int statusCode) {
        assertThat(lastResponse.statusCode())
                .as("Le code HTTP attendu est " + statusCode)
                .isEqualTo(statusCode);
    }

    @Alors("le message d'erreur contient {string}")
    public void le_message_derreur_contient(String texte) {
        String message = lastResponse.jsonPath().getString("message");
        assertThat(message)
                .as("Le message d'erreur devrait contenir '" + texte + "'")
                .containsIgnoringCase(texte);
    }

    @Alors("la réponse contient le nom {string}")
    public void la_reponse_contient_le_nom(String nom) {
        String actualName = lastResponse.jsonPath().getString("name");
        assertThat(actualName)
                .as("Le nom dans la réponse devrait être " + nom)
                .isEqualTo(nom);
    }

    @Alors("la liste contient {int} agence(s)")
    public void la_liste_contient_n_agences(int nombre) {
        List<?> agences = lastResponse.jsonPath().getList("$");
        assertThat(agences)
                .as("La liste devrait contenir " + nombre + " agence(s)")
                .hasSize(nombre);
    }

    @Alors("la liste contient l'agence {string}")
    public void la_liste_contient_lagence(String nom) {
        List<String> noms = lastResponse.jsonPath().getList("name");
        assertThat(noms)
                .as("La liste devrait contenir une agence nommée '" + nom + "'")
                .contains(nom);
    }
}
