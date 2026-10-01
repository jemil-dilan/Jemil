package cm.nyi.auth.application.inbound.usecase;

import cm.nyi.auth.domain.user.UserId;
import cm.nyi.auth.domain.user.UserRole;
import java.util.Set;

public interface RegisterUserUseCase {
    UserId execute(Command command);

    record Command(String email, String password, String phoneNumber, Set<UserRole> roles) {}
}
