package cm.jemil.shared.config.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("test-secret-key-that-is-at-least-32-bytes-long-for-hmac", 900000, 604800000);
    }

    @Test
    void shouldGenerateAndValidateAccessToken() {
        var token = jwtService.generateAccessToken("user-123", Set.of("PASSENGER"));

        assertThat(token).isNotNull();
        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.extractUserId(token)).isEqualTo("user-123");
        assertThat(jwtService.extractRoles(token)).containsExactly("PASSENGER");
    }

    @Test
    void shouldGenerateAndValidateRefreshToken() {
        var token = jwtService.generateRefreshToken("user-123");

        assertThat(token).isNotNull();
        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.extractUserId(token)).isEqualTo("user-123");
    }

    @Test
    void shouldRejectInvalidToken() {
        assertThat(jwtService.isValid("invalid-token")).isFalse();
    }

    @Test
    void shouldRejectTamperedToken() {
        var token = jwtService.generateAccessToken("user-123", Set.of("ADMIN"));
        var tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertThat(jwtService.isValid(tampered)).isFalse();
    }

    @Test
    void shouldExtractMultipleRoles() {
        var token = jwtService.generateAccessToken("user-456", Set.of("ADMIN", "AGENCY_MANAGER"));

        assertThat(jwtService.extractRoles(token)).containsExactlyInAnyOrder("ADMIN", "AGENCY_MANAGER");
    }

    @Test
    void shouldReturnEmptyRolesForTokenWithoutRolesClaim() {
        var token = jwtService.generateRefreshToken("user-789");

        assertThat(jwtService.extractRoles(token)).isEmpty();
    }
}
