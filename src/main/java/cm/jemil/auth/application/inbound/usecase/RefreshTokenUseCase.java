package cm.jemil.auth.application.inbound.usecase;

import static cm.jemil.auth.domain.exception.AuthErrorCode.AUTH_401_002;

import cm.jemil.auth.domain.user.UserRepository;
import cm.jemil.shared.config.jwt.JwtService;
import cm.jemil.shared.exception.DomainException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public Result execute(String refreshToken) {
        if (!jwtService.isValid(refreshToken)) {
            throw new DomainException(AUTH_401_002);
        }
        String userId = jwtService.extractUserId(refreshToken);
        var user = userRepository
                .findById(new cm.jemil.auth.domain.user.UserId(java.util.UUID.fromString(userId)))
                .orElseThrow(() -> new DomainException(AUTH_401_002));

        var roleNames = user.getRoles().stream()
                .map(cm.jemil.auth.domain.user.UserRole::name)
                .collect(java.util.stream.Collectors.toSet());
        String newAccessToken = jwtService.generateAccessToken(userId, roleNames);
        return new Result(newAccessToken, refreshToken, userId, roleNames);
    }

    public record Result(String accessToken, String refreshToken, String userId, java.util.Set<String> roles) {}
}
