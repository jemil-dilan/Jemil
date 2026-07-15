package cm.jemil.auth.adapter.outbound.persistence.jpa.repository;

import cm.jemil.auth.adapter.outbound.persistence.jpa.entity.UserJpa;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSpringRepository extends JpaRepository<UserJpa, UUID> {
    Optional<UserJpa> findByEmail(String email);
}
