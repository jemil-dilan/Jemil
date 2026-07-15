package cm.jemil.auth.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);

    Optional<User> findById(UserId id);

    void insert(User user);
}
