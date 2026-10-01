package cm.nyi.auth.config;

import cm.nyi.auth.application.inbound.usecase.LoginUseCase;
import cm.nyi.auth.application.inbound.usecase.LoginUseCaseImpl;
import cm.nyi.auth.application.inbound.usecase.RefreshTokenUseCase;
import cm.nyi.auth.application.inbound.usecase.RegisterUserUseCase;
import cm.nyi.auth.application.inbound.usecase.RegisterUserUseCaseImpl;
import cm.nyi.auth.domain.user.UserRepository;
import cm.nyi.shared.config.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration("authApplicationBeans")
@RequiredArgsConstructor
public class AuthBeans {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepository userRepository) {
        return new RegisterUserUseCaseImpl(userRepository, passwordEncoder);
    }

    @Bean
    public LoginUseCase loginUseCase(UserRepository userRepository) {
        return new LoginUseCaseImpl(userRepository, passwordEncoder, jwtService);
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(UserRepository userRepository) {
        return new RefreshTokenUseCase(jwtService, userRepository);
    }
}
