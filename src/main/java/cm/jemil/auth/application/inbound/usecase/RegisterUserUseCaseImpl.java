package cm.jemil.auth.application.inbound.usecase;

import cm.jemil.auth.domain.exception.EmailAlreadyRegisteredException;
import cm.jemil.auth.domain.user.User;
import cm.jemil.auth.domain.user.UserId;
import cm.jemil.auth.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserId execute(Command command) {
        if (userRepository.findByEmail(command.email()).isPresent()) {
            throw new EmailAlreadyRegisteredException();
        }
        String hashedPassword = passwordEncoder.encode(command.password());
        User user = User.create(command.email(), hashedPassword, command.phoneNumber(), command.roles());
        userRepository.insert(user);
        return user.getId();
    }
}
