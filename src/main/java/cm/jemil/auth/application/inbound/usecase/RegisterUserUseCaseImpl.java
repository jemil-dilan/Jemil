package cm.jemil.auth.application.inbound.usecase;

import static cm.jemil.auth.domain.exception.AuthErrorCode.AUTH_409_001;

import cm.jemil.auth.domain.user.User;
import cm.jemil.auth.domain.user.UserId;
import cm.jemil.auth.domain.user.UserRepository;
import cm.jemil.shared.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserId execute(Command command) {
        if (userRepository.findByEmail(command.email()).isPresent()) {
            throw new DomainException(AUTH_409_001);
        }
        String hashedPassword = passwordEncoder.encode(command.password());
        User user = User.create(command.email(), hashedPassword, command.phoneNumber(), command.roles());
        userRepository.insert(user);
        return user.getId();
    }
}
