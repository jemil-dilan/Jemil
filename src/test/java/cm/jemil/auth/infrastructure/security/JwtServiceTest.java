package cm.jemil.auth.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private static final String SECRET = "mySecretKeyForTestingWhichIsLongEnoughToSatisfyHS256AlgorithmRequirements";
    private static final long EXPIRATION = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", EXPIRATION);
    }

    @Test
    void shouldGenerateValidToken() {
        UUID userId = UUID.randomUUID();
        String role = "ADMIN";

        String token = jwtService.generateToken(userId, role);

        assertThat(token).isNotEmpty();
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void shouldExtractUserIdFromToken() {
        UUID userId = UUID.randomUUID();
        String role = "USER";
        String token = jwtService.generateToken(userId, role);

        UUID extractedUserId = jwtService.extractUserId(token);

        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    void shouldExtractRoleFromToken() {
        UUID userId = UUID.randomUUID();
        String role = "CONTROLLER";
        String token = jwtService.generateToken(userId, role);

        String extractedRole = jwtService.extractRole(token);

        assertThat(extractedRole).isEqualTo(role);
    }

    @Test
    void shouldBeInvalidIfTokenIsModified() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "USER");
        String modifiedToken = token + "modified";

        assertThat(jwtService.isTokenValid(modifiedToken)).isFalse();
    }
}
