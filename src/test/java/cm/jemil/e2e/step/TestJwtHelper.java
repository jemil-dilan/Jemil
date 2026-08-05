package cm.jemil.e2e.step;

import cm.jemil.shared.config.jwt.JwtService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Convenience helpers for role-based tokens. Prefer the CommonSteps Gherkin
 * "I am connected as user id ... with the roles ..." for real user simulation.
 */
@Component
@RequiredArgsConstructor
public class TestJwtHelper {

    private final JwtService jwtService;

    public String token(String userId, Set<String> roles) {
        return jwtService.generateAccessToken(userId, roles);
    }

    public String adminToken() {
        return token("admin-user-id", Set.of("ADMIN"));
    }

    public String agencyManagerToken() {
        return token("manager-user-id", Set.of("AGENCY_MANAGER"));
    }

    public String passengerToken() {
        return token("passenger-user-id", Set.of("PASSENGER"));
    }
}
