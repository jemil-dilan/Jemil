package cm.jemil.booking.adapter.outbound.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "buses")
public class BusJpa {

    @Id
    private UUID id;

    @Column(name = "agency_id", nullable = false)
    private UUID agencyId;

    @Column(nullable = false, length = 120)
    private String label;

    @Column(nullable = false, length = 40)
    private String plate;

    @Column(name = "seat_count", nullable = false)
    private int seatCount;

    @Column(name = "seat_layout", nullable = false, length = 32)
    private String seatLayout;
}
