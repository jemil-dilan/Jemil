package cm.jemil.shared.config.jwt;

import static org.assertj.core.api.Assertions.assertThat;

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
        var token = jwtService.generateAccessToken("user-123", "PASSENGER");

        assertThat(token).isNotNull();
        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.extractUserId(token)).isEqualTo("user-123");
        assertThat(jwtService.extractRole(token)).isEqualTo("PASSENGER");
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
        var token = jwtService.generateAccessToken("user-123", "ADMIN");
        var tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertThat(jwtService.isValid(tampered)).isFalse();
    }

    @Test
    void shouldExtractAdminRole() {
        var token = jwtService.generateAccessToken("user-456", "ADMIN");

        assertThat(jwtService.extractRole(token)).isEqualTo("ADMIN");
    }

    @Test
    void shouldExtractControllerRole() {
        var token = jwtService.generateAccessToken("user-789", "CONTROLLER");

        assertThat(jwtService.extractRole(token)).isEqualTo("CONTROLLER");
    }
}
