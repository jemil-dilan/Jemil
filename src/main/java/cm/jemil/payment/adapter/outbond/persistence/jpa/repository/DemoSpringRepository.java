package cm.jemil.payment.adapter.outbond.persistence.jpa.repository;

import cm.jemil.payment.adapter.outbond.persistence.jpa.entity.DemoJpa;
import cm.jemil.payment.demo.view.DemoView.DemoView1;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DemoSpringRepository extends JpaRepository<DemoJpa, UUID> {

    @Query("""
          SELECT new cm.jemil.payment.demo.view.DemoView.DemoView1(
                t.id,
                 t.name
          )
          FROM DemoJpa t
          WHERE t.id = :demoID
      """)
    Optional<DemoView1> findByIdAsView1(@Param("demoID") UUID demoID);

    @Query("""
          SELECT new cm.jemil.payment.demo.view.DemoView.DemoView1(
                t.id,
                 t.name
          )
          FROM DemoJpa t
      """)
    List<DemoView1> findAllAsView1();
}
