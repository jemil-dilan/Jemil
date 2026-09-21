package cm.jemil.auth.application.inbound.usecase;

import cm.jemil.auth.domain.exception.InvalidCredentialsException;
import cm.jemil.auth.domain.user.User;
import cm.jemil.auth.domain.user.UserRepository;
import cm.jemil.auth.domain.user.UserRole;
import cm.jemil.shared.config.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class LoginUseCaseImpl implements LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public Result execute(Command command) {
        User user = userRepository.findByEmail(command.email()).orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (!user.isActive()) {
            throw new InvalidCredentialsException();
        }

        var roleNames = user.getRoles().stream().map(UserRole::name).collect(java.util.stream.Collectors.toSet());
        String accessToken = jwtService.generateAccessToken(user.getId().value().toString(), roleNames);
        String refreshToken =
                jwtService.generateRefreshToken(user.getId().value().toString());

        return new Result(accessToken, refreshToken, user.getId().value().toString(), roleNames);
    }
}
