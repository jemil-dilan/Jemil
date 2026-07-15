package cm.jemil.auth.application.inbound.usecase;

import java.util.Set;

public interface LoginUseCase {
    Result execute(Command command);

    record Command(String email, String password) {}

    record Result(String accessToken, String refreshToken, String userId, Set<String> roles) {}
}
