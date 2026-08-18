package cm.jemil.booking.adapter.outbound.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class BookingJpa {

    @Id
    private UUID id;

    @Column(nullable = false, length = 16, unique = true)
    private String ref;

    @Column(name = "trip_id", nullable = false)
    private UUID tripId;

    @Column(nullable = false, length = 32)
    private String channel;

    @Column(name = "passenger_name", nullable = false, length = 160)
    private String passengerName;

    @Column(name = "passenger_msisdn", length = 32)
    private String passengerMsisdn;

    @Column(name = "amount_xaf", nullable = false)
    private int amountXaf;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "hold_expires_at")
    private OffsetDateTime holdExpiresAt;

    @Column(name = "sold_by_staff_id")
    private UUID soldByStaffId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
