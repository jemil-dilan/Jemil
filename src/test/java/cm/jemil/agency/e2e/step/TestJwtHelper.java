package cm.jemil.agency.e2e.step;

import cm.jemil.shared.config.jwt.JwtService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestJwtHelper {

    private final JwtService jwtService;

    String adminToken() {
        return jwtService.generateAccessToken("admin-user-id", Set.of("ADMIN"));
    }

    String agencyManagerToken() {
        return jwtService.generateAccessToken("manager-user-id", Set.of("AGENCY_MANAGER"));
    }

    String passengerToken() {
        return jwtService.generateAccessToken("passenger-user-id", Set.of("PASSENGER"));
    }
}
