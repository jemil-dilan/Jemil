package cm.jemil.auth.config;

import cm.jemil.auth.application.inbound.usecase.CreateDemoUseCase;
import cm.jemil.auth.application.inbound.usecase.GetAllDemoUseCase;
import cm.jemil.auth.application.inbound.usecase.GetDemoByIdUseCase;
import cm.jemil.auth.application.inbound.usecase.LoginUseCase;
import cm.jemil.auth.application.inbound.usecase.LoginUseCaseImpl;
import cm.jemil.auth.application.inbound.usecase.RefreshTokenUseCase;
import cm.jemil.auth.application.inbound.usecase.RegisterUserUseCase;
import cm.jemil.auth.application.inbound.usecase.RegisterUserUseCaseImpl;
import cm.jemil.auth.domain.demo.DemoRepository;
import cm.jemil.auth.domain.user.UserRepository;
import cm.jemil.shared.config.jwt.JwtService;
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

    @Bean("authCreateDemoUseCase")
    public CreateDemoUseCase createDemoUseCase(DemoRepository demoRepository) {
        return new CreateDemoUseCase(demoRepository);
    }

    @Bean("authGetAllDemoUseCase")
    public GetAllDemoUseCase getAllDemoUseCase(DemoRepository demoRepository) {
        return new GetAllDemoUseCase(demoRepository);
    }

    @Bean("authGetDemoByIdUseCase")
    public GetDemoByIdUseCase getDemoByIdUseCase(DemoRepository demoRepository) {
        return new GetDemoByIdUseCase(demoRepository);
    }
}
