package cm.jemil.auth.adapter.outbound.persistence.jpa.repository;

import cm.jemil.auth.adapter.outbound.persistence.jpa.repository.mapper.UserJpaMapper;
import cm.jemil.auth.domain.user.User;
import cm.jemil.auth.domain.user.UserId;
import cm.jemil.auth.domain.user.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {

    private final UserSpringRepository springRepository;
    private final UserJpaMapper mapper;

    @Override
    public Optional<User> findByEmail(String email) {
        return springRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return springRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public void insert(User user) {
        springRepository.save(mapper.toJpa(user));
    }
}
