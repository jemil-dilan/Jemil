package cm.nyi.auth.application.inbound.usecase;

import cm.nyi.auth.domain.exception.InvalidRefreshTokenException;
import cm.nyi.auth.domain.user.UserRepository;
import cm.nyi.shared.config.jwt.JwtService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public Result execute(String refreshToken) {
        if (!jwtService.isValid(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }
        String userId = jwtService.extractUserId(refreshToken);
        var user = userRepository
                .findById(new cm.nyi.auth.domain.user.UserId(java.util.UUID.fromString(userId)))
                .orElseThrow(InvalidRefreshTokenException::new);

        var roleNames = user.getRoles().stream()
                .map(cm.nyi.auth.domain.user.UserRole::name)
                .collect(java.util.stream.Collectors.toSet());
        String newAccessToken = jwtService.generateAccessToken(userId, roleNames);
        return new Result(newAccessToken, refreshToken, userId, roleNames);
    }

    public record Result(String accessToken, String refreshToken, String userId, java.util.Set<String> roles) {}
}
