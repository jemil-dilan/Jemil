package cm.jemil.auth.adapter.outbond.persistence.jpa.repository;

import cm.jemil.auth.adapter.outbond.persistence.jpa.entity.DemoJpa;
import cm.jemil.auth.domain.demo.view.DemoView.DemoView1;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthDemoSpringRepository extends JpaRepository<DemoJpa, UUID> {

    @Query("SELECT new cm.jemil.auth.domain.demo.view.DemoView$DemoView1(d.id, d.name) FROM AuthDemoJpa d")
    List<DemoView1> findAllAsView1();

    @Query(
            "SELECT new cm.jemil.auth.domain.demo.view.DemoView$DemoView1(d.id, d.name) FROM AuthDemoJpa d WHERE d.id = :id")
    Optional<DemoView1> findByIdAsView1(UUID id);
}
